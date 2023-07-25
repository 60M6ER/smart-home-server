package ru.bomber.smarthomeserver.services.telegram.command;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.bomber.core.trader.models.BotDTO;
import ru.bomber.core.trader.models.ExchangeVendor;
import ru.bomber.core.trader.models.Instrument;
import ru.bomber.smarthomeserver.model.User;
import ru.bomber.smarthomeserver.services.TelegramService;
import ru.bomber.smarthomeserver.services.telegram.KeyboardCreator;
import ru.bomber.smarthomeserver.services.telegram.TelegramConstants;
import ru.bomber.smarthomeserver.services.trader.TraderService;
import java.util.*;

@Component
public class BotsMenuCommandHandler extends BaseCommandHandler implements CommandService{

    private static final String NAME_MESSAGE = "Создаем бота\n" +
            "Введите его имя:";

    private static final String EXCHANGE_MESSAGE = "Вы ввели имя: %s\n" +
            "Выберетие биржу:";
    private static final String VENDOR_ERROR = "Вы ввели не поддрживаемую биржу. Повторите ввод.";

    private static final String PAIR_NAME_PART = "Введите сторку для поиска инструмена:";
    private static final String FAIL_PAIR_NAME_PART = "Инструменты не найдены. Повторите ввод:";


    private final TraderService traderService;

    private BotDTO botDTO;

    @Autowired
    public BotsMenuCommandHandler(TelegramService tService, TraderService traderService) {
        super(tService);
        this.traderService = traderService;
    }

    @Override
    public void handCommand(String[] args, Long chatId, Update update, Optional<User> user) {
        if (args.length > 0 && args[0].equals("/start")) {
            tService.delListener(chatId);
            tService.onNewUpdate(args[0], chatId, update);
        }
        if (args.length > 0 && args[0].equals("/go_back")) {
            step-=2;
        }
        switch (step) {
            case 0 -> {
                if (user.isEmpty()) {
                    if (update.getMessage().isGroupMessage()){
                        tService.sendTextMessage(TelegramConstants.UNREGISTERED_MESSAGE_IN_GROUP, chatId);
                    } else {
                        tService.sendTextMessage(TelegramConstants.UNREGISTERED_MESSAGE, chatId);
                    }
                }else {
                    sendTradingMenu(chatId, update);
                    tService.addListener(chatId, this);
                }
                step++;
            }
            case 1 -> {
                if (args[0].equals("/trading_bots")) {
                    tService.addListener(chatId, this);
                    sendBotList(chatId, update);
                }
                step++;
            }
            case 2 -> {
                if (args[0].equals("/trading_create_bot") || botDTO != null) {
                    botDTO = new BotDTO();
                    tService.sendTextMessage(NAME_MESSAGE, chatId);
                }
                step++;
            }
            case 3 -> {
                if (botDTO != null) {
                    botDTO.setName(args[0]);
                    sendExchangeList(chatId, update,
                            String.format(EXCHANGE_MESSAGE, botDTO.getName()));
                }
                step++;
            }
            case 4 -> {
                if (botDTO != null) {
                    try {
                        botDTO.setVendor(ExchangeVendor.valueOf(args[0]));
                        tService.sendTextMessage(PAIR_NAME_PART, chatId);
                        step++;
                    } catch (IllegalArgumentException e) {
                        tService.sendTextMessage(VENDOR_ERROR, chatId);
                    }
                }

            }
            case 5 -> {
                if (botDTO != null) {
                    sendInstrumentsList(chatId, update, args[0]);
                }

            }
        }
    }

    private void sendTradingMenu(Long chatId, Update update) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> firstLine = new ArrayList<>();
        InlineKeyboardButton settingsButton = new InlineKeyboardButton();
        settingsButton.setText("Боты");
        settingsButton.setCallbackData("/trading_bots");
        firstLine.add(settingsButton);


        InlineKeyboardButton securityButton = new InlineKeyboardButton();
        securityButton.setText("Операции со счетами ботов");
        securityButton.setCallbackData("/trading_operations");
        firstLine.add(securityButton);


        rowsInline.add(firstLine);

        inlineKeyboardMarkup.setKeyboard(rowsInline);

        sendMenu(chatId, update, inlineKeyboardMarkup, "Выберите интерисующий раздел меню: ");
    }

    private void sendBotList(Long chatId, Update update) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> firstLine = new ArrayList<>();
        InlineKeyboardButton settingsButton = new InlineKeyboardButton();
        settingsButton.setText("Создать бота");
        settingsButton.setCallbackData("/trading_create_bot");
        firstLine.add(settingsButton);
        rowsInline.add(firstLine);

        traderService.getBotList("").stream().forEach(b -> {
            List<InlineKeyboardButton> line = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(b.getName());
            button.setCallbackData(b.getId().toString());
            line.add(button);
            rowsInline.add(line);
        });

        inlineKeyboardMarkup.setKeyboard(rowsInline);

        sendMenu(chatId, update, inlineKeyboardMarkup, "Список ботов: ");
    }

    private void sendExchangeList(Long chatId, Update update, String message) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        Arrays.stream(ExchangeVendor.values()).forEach(e -> {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(ExchangeVendor.getView(e));
            inlineKeyboardButton.setCallbackData(e.name());
            rowInline.add(inlineKeyboardButton);
            rowsInline.add(rowInline);
        });

        inlineKeyboardMarkup.setKeyboard(rowsInline);
        sendMenu(chatId, update, inlineKeyboardMarkup, message, true);
    }

    private void sendInstrumentsList(Long chatId, Update update, String name_part) {
        List<Instrument> instruments = traderService.getInstruments(botDTO.getVendor(), name_part);
        if (instruments.size() == 0) {
            tService.sendTextMessage(FAIL_PAIR_NAME_PART, chatId);
        } else {
            KeyboardCreator keyboardCreator = new KeyboardCreator(3);
            instruments.forEach(i -> keyboardCreator.addButton(i.getInstrument(), i.getInstrument()));

            sendMenu(chatId, update, keyboardCreator.build(), "Выбирите интсрумент для работы бота: ", true);
            step++;
        }
    }

    @Override
    public Long getDateCreate() {
        return dateCreate;
    }

    @PostConstruct
    private void registerCommand() {
        tService.registerCommand(this);
    }

    @Override
    public boolean isThisCommand(String text) {
        return text.equals("/trading");
    }

    @Override
    public CommandHandler getInstance() {
        return new BotsMenuCommandHandler(tService, traderService);
    }
}

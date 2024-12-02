package ru.bomber.smarthomeserver.services.telegram.command;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.bomber.smarthomeserver.model.User;
import ru.bomber.smarthomeserver.services.TelegramService;
import ru.bomber.smarthomeserver.services.telegram.TelegramConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class TraderMenuCommandHandler extends BaseCommandHandler implements CommandService{

    private long dateCreate;

    @Autowired
    public TraderMenuCommandHandler(TelegramService tService) {
        super(tService);
    }

    @Override
    public void handCommand(String[] args, Long chatId, Update update, Optional<User> user) {
        if (user.isEmpty()) {
            if (update.getMessage().isGroupMessage()){
                tService.sendTextMessage(TelegramConstants.UNREGISTERED_MESSAGE_IN_GROUP, chatId);
            } else {
                tService.sendTextMessage(TelegramConstants.UNREGISTERED_MESSAGE, chatId);
            }
        }else {
            sendTradingMenu(chatId, update);
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

        if (update.hasCallbackQuery()) {
            EditMessageReplyMarkup message = new EditMessageReplyMarkup();
            message.setChatId(chatId);
            message.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
            message.setReplyMarkup(inlineKeyboardMarkup);
            tService.getTHandler().sendMessage(message);
        } else {
            SendMessage message = new SendMessage(chatId.toString(), "Выберите интерисующий раздел меню: ");
            message.setReplyMarkup(inlineKeyboardMarkup);
            tService.sendMessage(message);
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
        return new TraderMenuCommandHandler(tService);
    }
}

package ru.larionov.smarthomeserver.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.larionov.smarthomeserver.exeptions.CannotSendMessage;
import ru.larionov.smarthomeserver.exeptions.CommandNotSupported;
import ru.larionov.smarthomeserver.exeptions.NotFoundParameter;
import ru.larionov.smarthomeserver.exeptions.UserNotFound;
import ru.larionov.smarthomeserver.model.User;
import ru.larionov.smarthomeserver.repository.UserRepository;
import ru.larionov.smarthomeserver.services.telegram.TelegramHandler;
import ru.larionov.smarthomeserver.services.telegram.command.CommandHandler;
import ru.larionov.smarthomeserver.services.telegram.command.CommandService;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramService {

    private final UserRepository userRepository;
    private final UserService userService;

    @Value("${telegram_bot.username}")
    private String botUserName;
    @Value("${telegram_bot.token}")
    private String token;

    private TelegramHandler tHandler;

    private static final List<CommandService> COMMAND_SERVICES = new ArrayList<>();
    private static final HashMap<Long, CommandHandler> listeners = new HashMap<>();

    @PostConstruct
    private void init() {
        try {
            log.info("Start init telegram bot");
            if (botUserName == null || botUserName.isBlank())
                throw new NotFoundParameter("telegram_bot.username");
            if (token == null || token.isBlank())
                throw new NotFoundParameter("telegram_bot.token");
            tHandler = new TelegramHandler(
                    new DefaultBotOptions(),
                    botUserName,
                    token,
                    this
            );
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(tHandler);
        } catch (Exception e) {
            log.error("Can not init telegram bot.", e);
        }
    }

    public void onNewUpdate(String text, Long chatId, Update update) {
        String[] message = text.trim()
                .replaceAll("\\s+", " ")
                .split(" ");
        if (message.length > 0) {
            try {
                CommandHandler commandHandler = getCommand(message[0], chatId);
                Optional<User> user = userRepository.getUserByChatId(
                        chatId);

                commandHandler.handCommand(message,
                        chatId,
                        update,
                        user);
            } catch (CommandNotSupported e) {
                log.info("I received unsupported command");
                tHandler.sndTextMsg(
                        update.getMessage().getChatId(),
                        "Прости, но я пока не могу поддержать беседу на свободную тему :(");
            }
        }else {
            log.info("I received empty message");
        }
    }

    public void sendStartMenu(Long chatId) {
        List<KeyboardButton> buttonsMenu = new ArrayList<>();
        buttonsMenu.add(KeyboardButton.builder().text("Настройки").build());
        buttonsMenu.add(KeyboardButton.builder().text("Безопастность").build());
        buttonsMenu.add(KeyboardButton.builder().text("Торговля").build());
        ReplyKeyboardMarkup markup = ReplyKeyboardMarkup.builder().keyboardRow(new KeyboardRow(buttonsMenu))
                .resizeKeyboard(true).build();
        SendMessage message = new SendMessage(chatId.toString(), "");
        message.setReplyMarkup(markup);
        tHandler.sendMessage(message);
    }

    public void registerCommand(CommandService commandService){
        COMMAND_SERVICES.add(commandService);
    }

    public void addListener(Long chatId, CommandHandler commandHandler){
        listeners.put(chatId, commandHandler);
    }

    public void delListener(Long chatId){
        listeners.remove(chatId);
    }

    @Transactional
    public User getUserByTelegramCode(String code, Long chatId) throws UserNotFound {
        Long userId = userService.getUserIdByCodeTelegram(code);
        User user = userRepository.findById(userId).get();
        user.setChatId(chatId);
        return user;
    }

    public void sendTextMessage(String message, Long chatId) throws CannotSendMessage {
        tHandler.sendMessage(new SendMessage(chatId.toString(), message));
    }

    public void sendMessage(SendMessage message){
        tHandler.sendMessage(message);
    }

    private CommandHandler getCommand(String text, Long chatId) throws CommandNotSupported {
        if (listeners.containsKey(chatId)) {
            return listeners.get(chatId);
        }

        Optional<CommandService> command = COMMAND_SERVICES.stream().filter(c -> c.isThisCommand(text)).findFirst();

        if (command.isEmpty()) {
            throw new CommandNotSupported(text);
        }else {
            return command.get().getInstance();
        }
    }
}

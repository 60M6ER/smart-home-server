package ru.bomber.smarthomeserver.services.telegram.command;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.bomber.smarthomeserver.model.Role;
import ru.bomber.smarthomeserver.model.User;
import ru.bomber.smarthomeserver.model.telegram.Chat;
import ru.bomber.smarthomeserver.model.telegram.TelegramChatType;
import ru.bomber.smarthomeserver.repository.UserRepository;
import ru.bomber.smarthomeserver.repository.telegram.ChatRepository;
import ru.bomber.smarthomeserver.services.TelegramService;
import ru.bomber.smarthomeserver.services.telegram.TelegramConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class RegistrateGroupCommandHandler extends BaseCommandHandler implements CommandService{

    public static final String SELECT_TYPE =
            """
            Выберите тип этого чата:
            """;

    public static final String CHAT_WAS_SAVE =
            """
            Настроки этого чата сохранены. Ожидайте сообщений.
            """;

    private UserRepository userRepository;
    private ChatRepository chatRepository;
    private long dateCreate;

    @Autowired
    public RegistrateGroupCommandHandler(TelegramService tService, UserRepository userRepository, ChatRepository chatRepository) {
        super(tService);
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
    }

    @Override
    public void handCommand(String[] args, Long chatId, Update update, Optional<User> user) {

        switch (step) {
            case 0:
                if (update.getMessage().isGroupMessage()) {
                } else {
                    tService.sendTextMessage(TelegramConstants.BAD_COMMAND, chatId);
                    break;
                }

                step++;
                if (args.length < 2) {
                    tService.sendTextMessage(TelegramConstants.USER_NOT_FOUND, chatId);
                    break;
                }
                Optional<User> userByLogin = userRepository.findByUsername(args[1]);
                if (userByLogin.isEmpty() || !userByLogin.get().getRoles().contains(Role.ROLE_ADMINISTRATOR)) {
                    tService.sendTextMessage(TelegramConstants.USER_NOT_FOUND, chatId);
                } else {
                    SendMessage sendMessage = new SendMessage(
                            chatId.toString(),
                            SELECT_TYPE);

                    sendMessage.setReplyMarkup(getKeyboardTypes());
                    tService.addListener(chatId, this);
                    tService.sendMessage(sendMessage);
                }
                break;
            case 1:
                tService.delListener(chatId);
                TelegramChatType type = TelegramChatType.valueOf(args[0]);
                Chat chat = new Chat();
                chat.setId(chatId);
                chat.setName(TelegramChatType.getRussianName(type));
                chat.setType(type);
                chatRepository.save(chat);
                tService.sendTextMessage(CHAT_WAS_SAVE, chatId);
                break;
        }
    }


    private InlineKeyboardMarkup getKeyboardTypes() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        Arrays.stream(TelegramChatType.values()).forEach(e -> {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(TelegramChatType.getRussianName(e));
            inlineKeyboardButton.setCallbackData(e.name());
            rowInline.add(inlineKeyboardButton);
            rowsInline.add(rowInline);
        });

        inlineKeyboardMarkup.setKeyboard(rowsInline);
        return inlineKeyboardMarkup;
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
        return text.equals("/registration_chat");
    }

    @Override
    public CommandHandler getInstance() {
        return new RegistrateGroupCommandHandler(tService, userRepository, chatRepository);
    }
}

package ru.bomber.smarthomeserver.services.telegram.command;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.bomber.smarthomeserver.services.telegram.TelegramConstants;
import ru.bomber.smarthomeserver.exeptions.UserNotFound;
import ru.bomber.smarthomeserver.model.User;
import ru.bomber.smarthomeserver.services.TelegramService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class RegistrationCommandHandler extends BaseCommandHandler implements CommandService{

    private static final String UNREGISTERED_MESSAGE =
            """
            Для того чтобы я тебя запомнил тебе необходим ключ регистрации.
            Отправь его мне в сообщении.
            Ключ можно получить в веб интерфейсе на странице своего пользователя, или запросить его у Хозяина
            """;

    private static final String INCORRECT_CODE =
            """
            Вы ввели не правильный код. Еще есть время попробовать
            """;

    private static final String REGISTRATION_SUCCESSFUL =
            """
            Будем знакомы, %s
            """;

    private static final String NEED_CHANGE_PASSWORD =
            """
            
            Вам необходимо сменить пароль.
            Можем сделать это прямо сейчас. %s, нажмите кнопку ниже.
            """;


    @Autowired
    public RegistrationCommandHandler(TelegramService tService) {
        super(tService);
    }

    @Override
    public void handCommand(String[] args, Long chatId, Update update, Optional<User> user) {
        switch (step) {
            case 0:
                if (user.isEmpty()) {
                    step++;
                    tService.addListener(chatId, this);
                    tService.sendTextMessage(UNREGISTERED_MESSAGE, chatId);
                }else {
                    tService.sendTextMessage(
                            String.format(TelegramConstants.REGISTERED_MESSAGE,
                                    user.get().getName()),
                            chatId);
                }
                break;
            case 1:
                try {
                    User fUser = tService.getUserByTelegramCode(args[0], chatId);
                    tService.delListener(chatId);
                    if (!fUser.isNeedChangePassword()) {
                        tService.sendTextMessage(
                                String.format(REGISTRATION_SUCCESSFUL, fUser.getName()),
                                chatId
                        );
                    }else {
                        StringBuilder sb = new StringBuilder(String.format(REGISTRATION_SUCCESSFUL, fUser.getName())).append("\n");
                        sb.append(String.format(NEED_CHANGE_PASSWORD, fUser.getName()));
                        SendMessage sendMessage = new SendMessage(
                                chatId.toString(),
                                sb.toString());
                        InlineKeyboardMarkup inlineKeyboardMarkup =new InlineKeyboardMarkup();
                        List<InlineKeyboardButton> buttons = new ArrayList<>();
                        buttons.add(
                                InlineKeyboardButton.builder()
                                        .text("Изменить пароль").
                                        callbackData("/change_password")
                                        .build());
                        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                        keyboard.add(buttons);
                        inlineKeyboardMarkup.setKeyboard(keyboard);
                        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                        tService.sendMessage(sendMessage);
                    }
                } catch (UserNotFound e) {
                    tService.sendTextMessage(INCORRECT_CODE,
                            chatId);
                }
        }
    }

    @PostConstruct
    private void registerCommand() {
        tService.registerCommand(this);
    }

    @Override
    public boolean isThisCommand(String text) {
        return text.equals("/registration");
    }

    @Override
    public CommandHandler getInstance() {
        return new RegistrationCommandHandler(tService);
    }

}

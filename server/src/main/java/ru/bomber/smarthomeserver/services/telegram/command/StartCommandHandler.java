package ru.bomber.smarthomeserver.services.telegram.command;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.bomber.smarthomeserver.services.telegram.TelegramConstants;
import ru.bomber.smarthomeserver.model.User;
import ru.bomber.smarthomeserver.services.TelegramService;

import java.util.Optional;

@Component
public class StartCommandHandler extends BaseCommandHandler implements CommandService{

    public static final String UNREGISTERED_MESSAGE =
            """
            Привет, я дом.
            Давай знакомитсья.
            Для знакомства отправь команду /registration
            """;

    public static final String UNREGISTERED_MESSAGE_IN_GROUP =
            """
            Привет, я дом.
            Давай знакомитсья.
            Для знакомства отправь команду /registration_chat [your_login]
            """;

    private long dateCreate;

    @Autowired
    public StartCommandHandler(TelegramService tService) {
        super(tService);
    }

    @Override
    public void handCommand(String[] args, Long chatId, Update update, Optional<User> user) {
        if (user.isEmpty()) {
            if (update.getMessage().isGroupMessage()){
                tService.sendTextMessage(UNREGISTERED_MESSAGE_IN_GROUP, chatId);
            } else {
                tService.sendTextMessage(UNREGISTERED_MESSAGE, chatId);
            }
        }else {
            tService.sendTextMessage(
                    String.format(TelegramConstants.REGISTERED_MESSAGE,
                            user.get().getName()),
                    chatId);
            tService.sendStartMenu(chatId);
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
        return text.equals("/start");
    }

    @Override
    public CommandHandler getInstance() {
        return new StartCommandHandler(tService);
    }
}

package ru.larionov.smarthomeserver.services.telegram.command;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.larionov.smarthomeserver.model.User;
import ru.larionov.smarthomeserver.services.TelegramService;
import ru.larionov.smarthomeserver.services.telegram.TelegramConstants;

import java.util.Optional;

@Component
public class StartCommandHandler extends BaseCommandHandler implements CommandService{

    public static final String UNREGISTERED_MESSAGE =
            """
            Привет, я дом.
            Давай знакомитсья.
            Для знакомства отправь команду /registration
            """;
    private long dateCreate;

    @Autowired
    public StartCommandHandler(TelegramService tService) {
        super(tService);
    }

    @Override
    public void handCommand(String[] args, Long chatId, Update update, Optional<User> user) {
        if (user.isEmpty()) {
            tService.sendTextMessage(UNREGISTERED_MESSAGE, chatId);
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

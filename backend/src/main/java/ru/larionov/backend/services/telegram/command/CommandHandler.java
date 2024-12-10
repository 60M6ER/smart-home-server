package ru.larionov.backend.services.telegram.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.larionov.backend.model.User;
import java.util.Optional;

public interface CommandHandler {

    void handCommand(String[] args, Long chatId, Update update, Optional<User> user);

    Long getDateCreate();
}

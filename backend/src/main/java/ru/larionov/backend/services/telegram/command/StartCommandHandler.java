package ru.larionov.backend.services.telegram.command;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.larionov.backend.model.User;
import ru.larionov.backend.services.TelegramService;
import ru.larionov.backend.services.telegram.TelegramConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class StartCommandHandler extends BaseCommandHandler implements CommandService{

    private long dateCreate;

    @Autowired
    public StartCommandHandler(TelegramService tService) {
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
            sendStartMenu(chatId,
                    update,
                    String.format(TelegramConstants.REGISTERED_MESSAGE,
                            user.get().getName()));
        }
    }

    private void sendStartMenu(Long chatId, Update update, String message) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> firstLine = new ArrayList<>();
        InlineKeyboardButton settingsButton = new InlineKeyboardButton();
        settingsButton.setText("Натсройки");
        settingsButton.setCallbackData("/settings");
        firstLine.add(settingsButton);


        InlineKeyboardButton securityButton = new InlineKeyboardButton();
        securityButton.setText("Безопасность");
        securityButton.setCallbackData("/security");
        firstLine.add(securityButton);

        List<InlineKeyboardButton> secondLine = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Торговля");
        inlineKeyboardButton.setCallbackData("/trading");
        secondLine.add(inlineKeyboardButton);

        rowsInline.add(firstLine);
        rowsInline.add(secondLine);

        inlineKeyboardMarkup.setKeyboard(rowsInline);

        sendMenu(chatId, update, inlineKeyboardMarkup, message + "\nВыберите интерисующий раздел меню: ");
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

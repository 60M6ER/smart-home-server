package ru.bomber.smarthomeserver.services.telegram.command;

import lombok.Builder;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.bomber.smarthomeserver.services.TelegramService;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCommandHandler implements CommandHandler {

    protected TelegramService tService;
    protected Long dateCreate;

    protected int step;

    public BaseCommandHandler(TelegramService tService) {
        this.tService = tService;
        dateCreate = System.currentTimeMillis();
        step = 0;
    }

    protected void sendMenu(Long chatId, Update update, InlineKeyboardMarkup inlineKeyboardMarkup, String editText) {
        sendMenu(chatId, update, inlineKeyboardMarkup, editText, false);
    }

    protected void sendMenu(Long chatId, Update update, InlineKeyboardMarkup inlineKeyboardMarkup, String editText,
                            boolean addBackButton) {
        if (addBackButton) {
            inlineKeyboardMarkup = addBackButton(inlineKeyboardMarkup);
        }
        if (inlineKeyboardMarkup != null) {
            List<InlineKeyboardButton> line = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText("Домой");
            button.setCallbackData("/start");
            line.add(button);
            inlineKeyboardMarkup.getKeyboard().add(line);
        }

        if (update.hasCallbackQuery()) {
            EditMessageText messageText = new EditMessageText();
            messageText.setChatId(chatId);
            messageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
            messageText.setText(editText);
            messageText.setReplyMarkup(inlineKeyboardMarkup);
            tService.getTHandler().sendMessage(messageText);
        } else {
            SendMessage message = new SendMessage(chatId.toString(), editText);
            message.setReplyMarkup(inlineKeyboardMarkup);
            tService.sendMessage(message);
        }
    }

    private InlineKeyboardMarkup addBackButton(InlineKeyboardMarkup inlineKeyboardMarkup) {
        if (inlineKeyboardMarkup != null) {
            inlineKeyboardMarkup.getKeyboard().add(getGoBack());
        } else {
            inlineKeyboardMarkup = new InlineKeyboardMarkup();

            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            rowsInline.add(getGoBack());

            inlineKeyboardMarkup.setKeyboard(rowsInline);
        }
        return inlineKeyboardMarkup;
    }

    private List<InlineKeyboardButton> getGoBack() {
        List<InlineKeyboardButton> line = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Назад");
        button.setCallbackData("/go_back");
        line.add(button);
        return line;
    }

    @Override
    public Long getDateCreate() {
        return dateCreate;
    }
}

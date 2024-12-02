package ru.bomber.smarthomeserver.services.telegram;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class KeyboardCreator {

    private InlineKeyboardMarkup markup;

    private List<List<InlineKeyboardButton>> rowsInline;

    private List<InlineKeyboardButton> line;

    private int maxButtonsOnLine;

    public KeyboardCreator(int maxButtonsOnLine) {
        this.maxButtonsOnLine = maxButtonsOnLine;
        markup = new InlineKeyboardMarkup();
        rowsInline = new ArrayList<>();
        line = new ArrayList<>();
    }

    private void recreateLine() {
        rowsInline.add(line);
        line = new ArrayList<>();
    }

    public KeyboardCreator addButton(InlineKeyboardButton button) {
        if (line.size() == maxButtonsOnLine)
            recreateLine();
        line.add(button);
        return this;
    }

    public KeyboardCreator addButton(String text, String callBack) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callBack);
        return addButton(button);
    }

    public InlineKeyboardMarkup build() {
        rowsInline.add(line);
        markup.setKeyboard(rowsInline);
        return markup;
    }
}

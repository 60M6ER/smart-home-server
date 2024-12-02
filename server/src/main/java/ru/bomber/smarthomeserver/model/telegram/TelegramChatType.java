package ru.bomber.smarthomeserver.model.telegram;

public enum TelegramChatType {
    NOTIFICATION, TRADER_NOTIFICATION;

    public static String getRussianName(TelegramChatType type) {
        switch (type) {
            case NOTIFICATION -> {
                return "Уведомления";
            }
            case TRADER_NOTIFICATION -> {
                return "Уведомления по торговле";
            }
        }
        return "";
    }
}

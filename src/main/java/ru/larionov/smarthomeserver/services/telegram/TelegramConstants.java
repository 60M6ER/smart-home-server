package ru.larionov.smarthomeserver.services.telegram;

public class TelegramConstants {
    public static final String REGISTERED_MESSAGE =
            """
            Мы уже знакомы.
            Привет, %s
            """;


    public static final String UNAUTHORIZED_MESSAGE =
            """
            Я вас не знаю.
            Пройдите процедуру регитсрации.
            /registration
            """;
}

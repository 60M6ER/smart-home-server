package ru.bomber.smarthomeserver.services.telegram;

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

    public static final String BAD_COMMAND =
            """
            Команда не подходит для этого чата.
            """;

    public static final String USER_NOT_FOUND =
            """
            Пользователь с таким логином не найден или недостаточно прав.
            """;
}

package ru.bomber.smarthomeserver.services.telegram.command;


public interface CommandService {

    boolean isThisCommand(String text);

    CommandHandler getInstance();


}

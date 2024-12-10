package ru.larionov.backend.services.telegram.command;


public interface CommandService {

    boolean isThisCommand(String text);

    CommandHandler getInstance();


}

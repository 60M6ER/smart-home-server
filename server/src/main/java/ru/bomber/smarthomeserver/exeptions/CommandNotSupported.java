package ru.bomber.smarthomeserver.exeptions;

public class CommandNotSupported extends RuntimeException{
    public CommandNotSupported(String command) {
        super("Command not supported: " + command);
    }
}

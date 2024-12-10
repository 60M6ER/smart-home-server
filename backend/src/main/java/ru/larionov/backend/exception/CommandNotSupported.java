package ru.larionov.backend.exception;

public class CommandNotSupported extends RuntimeException{
    public CommandNotSupported(String command) {
        super("Command not supported: " + command);
    }
}

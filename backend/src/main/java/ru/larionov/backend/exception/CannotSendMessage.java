package ru.larionov.backend.exception;

public class CannotSendMessage extends RuntimeException{
    public CannotSendMessage(Throwable cause) {
        super(cause);
    }
}

package ru.bomber.smarthomeserver.exeptions;

public class CannotSendMessage extends RuntimeException{
    public CannotSendMessage(Throwable cause) {
        super(cause);
    }
}

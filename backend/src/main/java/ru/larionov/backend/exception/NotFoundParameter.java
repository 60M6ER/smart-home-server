package ru.larionov.backend.exception;

public class NotFoundParameter extends RuntimeException {
    public NotFoundParameter(String nameParameter) {
        super("Not found parameter: " + nameParameter);
    }
}

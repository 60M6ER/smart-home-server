package ru.larionov.smarthomeserver.exeptions;

public class NotFoundParameter extends RuntimeException {
    public NotFoundParameter(String nameParameter) {
        super("Not found parameter: " + nameParameter);
    }
}

package ru.bomber.smarthomeserver.services.telegram.command;

import ru.bomber.smarthomeserver.services.TelegramService;

public abstract class BaseCommandHandler implements CommandHandler {

    protected TelegramService tService;
    protected Long dateCreate;

    protected int step;

    public BaseCommandHandler(TelegramService tService) {
        this.tService = tService;
        dateCreate = System.currentTimeMillis();
        step = 0;
    }

    @Override
    public Long getDateCreate() {
        return dateCreate;
    }
}

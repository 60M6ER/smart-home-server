package ru.bomber.trader.utils.timer;

import java.util.UUID;

public interface TimerProvider {

    void addListener(TimerListener listener, String marker, long delay);
    void delListener(UUID id);
}

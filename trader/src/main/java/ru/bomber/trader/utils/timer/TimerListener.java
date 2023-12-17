package ru.bomber.trader.utils.timer;

import java.util.UUID;

public interface TimerListener {

    void onEventTimer(String marker);
    UUID getId();
}

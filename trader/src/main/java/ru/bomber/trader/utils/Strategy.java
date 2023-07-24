package ru.bomber.trader.utils;

import java.util.UUID;

public interface Strategy {

    void start();
    void stop();

    UUID getId();
}

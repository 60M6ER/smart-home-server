package ru.larionov.backend.model;

public enum ExchangeVendor {
    POLONIEX, PHEMEX;

    public static String getView(ExchangeVendor exchangeVendor) {
        return switch (exchangeVendor) {
            case POLONIEX -> "Poloniex";
            case PHEMEX -> "Phemex";
        };
    }
}

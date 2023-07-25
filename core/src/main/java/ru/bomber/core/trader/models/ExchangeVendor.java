package ru.bomber.core.trader.models;

public enum ExchangeVendor {
    TINKOFF, POLONIEX;

    public static String getView(ExchangeVendor exchangeVendor) {
        return switch (exchangeVendor) {
            case POLONIEX -> "Poloniex";
            case TINKOFF -> "Тинькофф";
        };
    }
}
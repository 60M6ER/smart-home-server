package ru.larionov.backend.model;

public enum ExchangeVendor {
    POLONIEX, PHEMEX, BINANCE, HIT_BTC;

    public static String getView(ExchangeVendor exchangeVendor) {
        return switch (exchangeVendor) {
            case POLONIEX -> "Poloniex";
            case PHEMEX -> "Phemex";
            case BINANCE -> "Binance";
            case HIT_BTC -> "HitBTC";
        };
    }
}

package ru.larionov.backend.dto.exchange.binance;

import lombok.Data;

@Data
public class BinancePriceTicker {
    private String symbol;
    private double price;
}

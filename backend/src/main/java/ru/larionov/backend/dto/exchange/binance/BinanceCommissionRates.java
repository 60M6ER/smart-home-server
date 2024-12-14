package ru.larionov.backend.dto.exchange.binance;

import lombok.Data;

@Data
public class BinanceCommissionRates {
    public String maker;
    public String taker;
    public String buyer;
    public String seller;
}

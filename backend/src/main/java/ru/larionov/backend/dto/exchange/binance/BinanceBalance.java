package ru.larionov.backend.dto.exchange.binance;

import lombok.Data;

@Data
public class BinanceBalance {
    public String asset;
    public String free;
    public String locked;
}

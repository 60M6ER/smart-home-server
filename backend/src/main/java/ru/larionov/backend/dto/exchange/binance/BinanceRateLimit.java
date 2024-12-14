package ru.larionov.backend.dto.exchange.binance;

import lombok.Data;

@Data
public class BinanceRateLimit {
    public String rateLimitType;
    public String interval;
    public int intervalNum;
    public int limit;
}

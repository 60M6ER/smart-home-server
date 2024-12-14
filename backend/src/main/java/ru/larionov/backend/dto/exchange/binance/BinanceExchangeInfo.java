package ru.larionov.backend.dto.exchange.binance;

import lombok.Data;

import java.util.ArrayList;

@Data
public class BinanceExchangeInfo {
    public String timezone;
    public long serverTime;
    public ArrayList<BinanceRateLimit> rateLimits;
    public ArrayList<Object> exchangeFilters;
    public ArrayList<BinanceSymbol> symbols;
}

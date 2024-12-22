package ru.larionov.backend.dto.exchange.binance;

import lombok.Data;

@Data
public class BinanceDepositAddress {
    private String address;
    private String coin;
    private String tag;
    private String url;
}

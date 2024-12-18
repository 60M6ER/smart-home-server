package ru.larionov.backend.dto.exchange.binance;

import lombok.Data;

@Data
public class BinanceUserAsset {

    private String asset;
    private double free;
    private double locked;
    private double freeze;
    private double withdrawing;
    private double ipoable;
    private double btcValuation;
}

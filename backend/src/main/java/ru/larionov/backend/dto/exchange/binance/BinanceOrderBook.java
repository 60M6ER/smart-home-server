package ru.larionov.backend.dto.exchange.binance;

import lombok.Data;

import java.util.ArrayList;

@Data
public class BinanceOrderBook {
    public long lastUpdateId;
    public ArrayList<ArrayList<String>> bids;
    public ArrayList<ArrayList<String>> asks;
}

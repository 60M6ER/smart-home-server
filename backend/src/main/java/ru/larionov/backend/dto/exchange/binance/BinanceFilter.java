package ru.larionov.backend.dto.exchange.binance;

import lombok.Data;

@Data
public class BinanceFilter {
    public String filterType;
    public String minPrice;
    public String maxPrice;
    public String tickSize;
    public double minQty;
    public double maxQty;
    public String stepSize;
    public int limit;
    public int minTrailingAboveDelta;
    public int maxTrailingAboveDelta;
    public int minTrailingBelowDelta;
    public int maxTrailingBelowDelta;
    public String bidMultiplierUp;
    public String bidMultiplierDown;
    public String askMultiplierUp;
    public String askMultiplierDown;
    public int avgPriceMins;
    public String minNotional;
    public boolean applyMinToMarket;
    public String maxNotional;
    public boolean applyMaxToMarket;
    public int maxNumOrders;
    public int maxNumAlgoOrders;
}

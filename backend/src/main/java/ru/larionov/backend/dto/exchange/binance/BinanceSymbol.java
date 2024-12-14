package ru.larionov.backend.dto.exchange.binance;

import lombok.Data;

import java.util.ArrayList;

@Data
public class BinanceSymbol {
    public String symbol;
    public String status;
    public String baseAsset;
    public int baseAssetPrecision;
    public String quoteAsset;
    public int quotePrecision;
    public int quoteAssetPrecision;
    public int baseCommissionPrecision;
    public int quoteCommissionPrecision;
    public ArrayList<String> orderTypes;
    public boolean icebergAllowed;
    public boolean ocoAllowed;
    public boolean otoAllowed;
    public boolean quoteOrderQtyMarketAllowed;
    public boolean allowTrailingStop;
    public boolean cancelReplaceAllowed;
    public boolean isSpotTradingAllowed;
    public boolean isMarginTradingAllowed;
    public ArrayList<BinanceFilter> filters;
    public ArrayList<Object> permissions;
    public ArrayList<ArrayList<String>> permissionSets;
    public String defaultSelfTradePreventionMode;
    public ArrayList<String> allowedSelfTradePreventionModes;
}

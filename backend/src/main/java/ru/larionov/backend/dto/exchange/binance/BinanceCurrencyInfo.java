package ru.larionov.backend.dto.exchange.binance;

import lombok.Data;

import java.util.ArrayList;

@Data
public class BinanceCurrencyInfo {
    private String coin;
    private boolean depositAllEnable;
    private String free;
    private String freeze;
    private String ipoable;
    private String ipoing;
    private boolean isLegalMoney;
    private String locked;
    private String name;
    private ArrayList<BinanceNetworkListItem> networkList;
    private String storage;
    private boolean trading;
    private boolean withdrawAllEnable;
    private String withdrawing;

    public void setIsLegalMoney(boolean isLegalMoney) {
        this.isLegalMoney = isLegalMoney;
    }
}

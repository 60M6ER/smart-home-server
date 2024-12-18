package ru.larionov.backend.dto.exchange.binance;

import lombok.Data;

@Data
public class BinanceNetworkListItem {
    private String addressRegex;
    private String coin;
    private String depositDesc;
    private boolean depositEnable;
    private boolean isDefault;
    private String memoRegex;
    private int minConfirm;
    private String name;
    private String network;
    private String specialTips;
    private int unLockConfirm;
    private String withdrawDesc;
    private boolean withdrawEnable;
    private double withdrawFee;
    private double withdrawIntegerMultiple;
    private double withdrawMax;
    private double withdrawMin;
    private String withdrawInternalMin;
    private boolean sameAddress;
    private int estimatedArrivalTime;
    private boolean busy;
    private String contractAddressUrl;
    private String contractAddress;
}

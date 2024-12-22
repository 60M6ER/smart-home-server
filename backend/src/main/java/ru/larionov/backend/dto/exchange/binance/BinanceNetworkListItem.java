package ru.larionov.backend.dto.exchange.binance;

import lombok.Data;

@Data
public class BinanceNetworkListItem {
    private String network;
    private String coin;
    private double withdrawIntegerMultiple;
    private boolean isDefault;
    private boolean depositEnable;
    private boolean withdrawEnable;
    private String depositDesc;
    private String withdrawDesc;
    private String specialTips;
    private String specialWithdrawTips;
    private String name;
    private boolean resetAddressStatus;
    private String addressRegex;
    private String memoRegex;
    private double withdrawFee;
    private double withdrawMin;
    private double withdrawMax;
    private double withdrawInternalMin;
    private double depositDust;
    private int minConfirm;
    private int unLockConfirm;
    private boolean sameAddress;
    private int estimatedArrivalTime;
    private boolean busy;
    private String contractAddressUrl;
    private String contractAddress;

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}

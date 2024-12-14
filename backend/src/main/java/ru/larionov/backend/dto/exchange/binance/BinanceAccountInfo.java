package ru.larionov.backend.dto.exchange.binance;

import lombok.Data;

import java.util.ArrayList;

@Data
public class BinanceAccountInfo {
    public int makerCommission;
    public int takerCommission;
    public int buyerCommission;
    public int sellerCommission;
    public BinanceCommissionRates commissionRates;
    public boolean canTrade;
    public boolean canWithdraw;
    public boolean canDeposit;
    public boolean brokered;
    public boolean requireSelfTradePrevention;
    public boolean preventSor;
    public long updateTime;
    public String accountType;
    public ArrayList<BinanceBalance> balances;
    public ArrayList<String> permissions;
    public int uid;
}

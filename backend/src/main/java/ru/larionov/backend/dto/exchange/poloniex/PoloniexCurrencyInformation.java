package ru.larionov.backend.dto.exchange.poloniex;

import lombok.Data;

import java.util.ArrayList;

@Data
public class PoloniexCurrencyInformation {
    private int id;
    private String coin;
    private boolean delisted;
    private boolean tradeEnable;
    private String name;
    private ArrayList<PoloniexNetworkListItem> networkList;
    private boolean supportCollateral;
    private boolean supportBorrow;
}

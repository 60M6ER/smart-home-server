package ru.larionov.backend.dto.exchange.poloniex;

import lombok.Data;

@Data
public class PoloniexNetworkListItem {
    private int id;
    private String coin;
    private String name;
    private String currencyType;
    private String blockchain;
    private boolean withdrawalEnable;
    private boolean depositEnable;
    private String depositAddress;
    private Double withdrawMin;
    private int decimals;
    private Double withdrawFee;
    private int minConfirm;
}

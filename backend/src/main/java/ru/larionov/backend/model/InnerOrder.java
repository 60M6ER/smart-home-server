package ru.larionov.backend.model;

import lombok.Data;

@Data
public class InnerOrder {
    private PairCurrency pair;
    private TypeOrder typeOrder;
    private TypeMarket typeMarket;
    private double price;
    private double quantity; // Base currency MARKET SELL and all LIMIT
    private double amount; // Quote currency MARKET BUY
    private String myOrderId;
    private boolean allowBorrow;
    private OrderState state;
}

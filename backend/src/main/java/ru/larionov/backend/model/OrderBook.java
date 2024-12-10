package ru.larionov.backend.model;

import lombok.Data;

import java.util.List;

@Data
public class OrderBook {
    private String pairToken;
    private double priceScale;
    private List<OrderBookRow> asks;
    private List<OrderBookRow> bids;

    public int getRowCount() {
        return asks.size();
    }

    public OrderBookRow getBestPriceByTypeOrder(TypeOrder typeOrder, int row) {
        if (row > getRowCount() - 1) {
            row = getRowCount() - 1;
        }
        if (typeOrder == TypeOrder.BUY)
            return asks.get(row);
        else
            return bids.get(row);
    }
}

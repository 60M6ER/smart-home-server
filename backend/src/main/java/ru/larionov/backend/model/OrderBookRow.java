package ru.larionov.backend.model;

import lombok.Data;

@Data
public class OrderBookRow {
    private double price;
    private double amount;
}

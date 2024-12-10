package ru.larionov.backend.model;

import lombok.Data;

@Data
public class PricePair {
    private double price;
    private String pairToken;
}

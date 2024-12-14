package ru.larionov.backend.model;

import lombok.Data;

import java.util.Objects;

@Data
public class PairCurrency {
    private String baseCurrency;
    private String quoteCurrency;

    private ExchangeVendor vendor;

    private int priceScale;
    private int amountScale;
    private int quantityScale;
    private double minQuantity; // Minimum for Base currency
    private double minAmount; // Minimum for Quote currency
    private String exchangeToken;
    private boolean marginTrading;

    public String getToken() {
        return baseCurrency + "_" + quoteCurrency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PairCurrency that = (PairCurrency) o;

        if (!baseCurrency.equals(that.baseCurrency)) return false;
        if (!quoteCurrency.equals(that.quoteCurrency)) return false;
        return vendor == that.vendor;
    }

    @Override
    public int hashCode() {
        int result = baseCurrency.hashCode();
        result = 31 * result + quoteCurrency.hashCode();
        result = 31 * result + vendor.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return quoteCurrency + " -> " + baseCurrency;
    }
}

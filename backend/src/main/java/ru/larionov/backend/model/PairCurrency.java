package ru.larionov.backend.model;

import lombok.Data;

import java.util.Objects;

@Data
public class PairCurrency {
    private String baseCurrency;
    private String quoteCurrency;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PairCurrency that = (PairCurrency) o;

        if (!Objects.equals(baseCurrency, that.baseCurrency)) return false;
        return Objects.equals(quoteCurrency, that.quoteCurrency);
    }

    @Override
    public int hashCode() {
        int result = baseCurrency != null ? baseCurrency.hashCode() : 0;
        result = 31 * result + (quoteCurrency != null ? quoteCurrency.hashCode() : 0);
        return result;
    }
}

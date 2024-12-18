package ru.larionov.backend.dto.portfolio;

import lombok.Data;
import ru.larionov.backend.model.ExchangeVendor;

@Data
public class CurrencyKey {
    private String name;
    private ExchangeVendor vendor;

    public CurrencyKey(String name, ExchangeVendor vendor) {
        this.name = name;
        this.vendor = vendor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CurrencyKey that = (CurrencyKey) o;

        if (!name.equals(that.name)) return false;
        return vendor == that.vendor;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + vendor.hashCode();
        return result;
    }
}

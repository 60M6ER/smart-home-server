package ru.larionov.backend.model;

import lombok.Data;

@Data
public class DepositAddress {
    private String address;
    private String currencyToken;
    private ExchangeVendor vendor;
}

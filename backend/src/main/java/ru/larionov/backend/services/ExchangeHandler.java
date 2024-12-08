package ru.larionov.backend.services;

import ru.larionov.backend.model.Currency;
import ru.larionov.backend.model.ExchangeVendor;

import java.util.List;

public interface ExchangeHandler {

    ExchangeVendor getVendor();

    void update();
    List<Currency> getPortfolio();

}

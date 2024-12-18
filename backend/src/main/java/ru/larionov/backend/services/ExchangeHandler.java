package ru.larionov.backend.services;

import ru.larionov.backend.dto.portfolio.CurrencyPermissions;
import ru.larionov.backend.model.*;

import java.util.List;

public interface ExchangeHandler {

    ExchangeVendor getVendor();
    void update();
    List<Currency> getBalances();
    List<PairCurrency> getPairs();
    FeeInformation getFee();
    List<PricePair> getMarketPrices();
    OrderBook getOrderBook(PairCurrency pairCurrency);
    List<PairCurrency> getMarkPrices();

    CurrencyPermissions getCurrencyPermissions(String currencyToken);

}

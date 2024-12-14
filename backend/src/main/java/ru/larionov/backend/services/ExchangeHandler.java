package ru.larionov.backend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.larionov.backend.model.*;

import java.util.List;

public interface ExchangeHandler {

    ExchangeVendor getVendor();
    void update();
    List<Currency> getPortfolio();
    List<PairCurrency> getPairs();
    FeeInformation getFee();
    List<PricePair> getMarketPrices();
    OrderBook getOrderBook(PairCurrency pairCurrency);

}

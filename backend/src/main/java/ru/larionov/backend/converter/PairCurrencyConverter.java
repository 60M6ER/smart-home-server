package ru.larionov.backend.converter;

import com.poloniex.api.client.spot.model.response.spot.Market;
import ru.larionov.backend.model.PairCurrency;

public class PairCurrencyConverter {

    public static PairCurrency fromPoloniexMarket(Market market) {
        PairCurrency pairCurrency = new PairCurrency();
        pairCurrency.setBaseCurrency(market.getBaseCurrencyName());
        pairCurrency.setQuoteCurrency(market.getQuoteCurrencyName());
        pairCurrency.setPriceScale(market.getSymbolTradeLimit().getPriceScale());
        pairCurrency.setAmountScale(market.getSymbolTradeLimit().getAmountScale());
        pairCurrency.setAmountScale(market.getSymbolTradeLimit().getQuantityScale());
        pairCurrency.setMinQuantity(Double.parseDouble(market.getSymbolTradeLimit().getMinQuantity()));
        pairCurrency.setMinAmount(Double.parseDouble(market.getSymbolTradeLimit().getMinAmount()));
        return pairCurrency;
    }
}

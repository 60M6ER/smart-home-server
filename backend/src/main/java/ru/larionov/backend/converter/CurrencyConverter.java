package ru.larionov.backend.converter;

import com.poloniex.api.client.spot.model.response.spot.AccountBalance;
import com.poloniex.api.client.spot.model.response.spot.Balance;
import ru.larionov.backend.dto.exchange.binance.BinanceUserAsset;
import ru.larionov.backend.model.Currency;
import ru.larionov.backend.model.ExchangeVendor;

import java.math.BigDecimal;

public class CurrencyConverter {

    public static Currency fromPoloniexCurrency(Balance balance) {
        Currency currency = new Currency();
        currency.setName(balance.getCurrency());
        currency.setAmount(Double.parseDouble(balance.getAvailable()));
        currency.setHoldAmount(Double.parseDouble(balance.getHold()));
        return currency;
    }

    public static Currency fromBinanceAsset(BinanceUserAsset userAsset) {
        Currency currency = new Currency();
        currency.setName(userAsset.getAsset());
        currency.setAmount(userAsset.getFree());
        currency.setHoldAmount(userAsset.getLocked());
        return currency;
    }
}

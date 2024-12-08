package ru.larionov.backend.converter;

import com.poloniex.api.client.spot.model.response.spot.AccountBalance;
import com.poloniex.api.client.spot.model.response.spot.Balance;
import ru.larionov.backend.model.Currency;
import ru.larionov.backend.model.ExchangeVendor;

import java.math.BigDecimal;

public class CurrencyConverter {

    public static Currency fromPoloniexCurrency(Balance balance) {
        Currency currency = new Currency();
        currency.setName(balance.getCurrency());
        currency.setAmount(Double.parseDouble(balance.getAvailable()));
        currency.setVendor(ExchangeVendor.POLONIEX);
        return currency;
    }
}

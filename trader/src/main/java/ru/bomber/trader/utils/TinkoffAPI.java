package ru.bomber.trader.utils;

import ru.bomber.trader.dto.FeeData;
import ru.bomber.core.trader.models.ExchangeVendor;
import ru.bomber.core.trader.models.Instrument;
import ru.bomber.trader.models.Order;
import ru.tinkoff.piapi.core.InvestApi;

import java.util.List;

public class TinkoffAPI implements ExchangeAPI{

    private final InvestApi client;
    public TinkoffAPI() {
        client = InvestApi.create("");
    }

    @Override
    public ExchangeVendor getVendor() {
        return ExchangeVendor.TINKOFF;
    }

    @Override
    public FeeData getFee() {
        return null;
    }

    @Override
    public List<Instrument> getInstruments(String instrument) {
        return null;
    }

    @Override
    public List<Instrument> getInstruments() {
        return null;
    }

    @Override
    public Instrument getInstrument(String instrumentName) {
        return null;
    }

    @Override
    public Order createOrReplaceOrder(Order order) {
        return null;
    }
}

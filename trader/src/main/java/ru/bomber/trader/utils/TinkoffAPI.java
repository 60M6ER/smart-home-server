package ru.bomber.trader.utils;

import ru.bomber.trader.dto.FeeData;
import ru.bomber.core.mqtt.trader.models.ExchangeVendor;
import ru.bomber.trader.dto.Instrument;
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
    public List<Instrument> getBalanceOfInstrument(String instrument) {
        return null;
    }

    @Override
    public List<Instrument> getBalanceOfInstrument() {
        return null;
    }

    @Override
    public Instrument getInstrument(String instrumentName) {
        return null;
    }
}

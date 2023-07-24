package ru.bomber.trader.utils;

import ru.bomber.trader.dto.FeeData;
import ru.bomber.trader.dto.Instrument;
import ru.bomber.core.mqtt.trader.models.ExchangeVendor;

import java.util.List;

public interface ExchangeAPI {

    ExchangeVendor getVendor();
    FeeData getFee();

    List<Instrument> getBalanceOfInstrument(String instrument);
    List<Instrument> getBalanceOfInstrument();

    Instrument getInstrument(String instrumentName);
}

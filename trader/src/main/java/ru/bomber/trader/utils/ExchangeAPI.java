package ru.bomber.trader.utils;

import ru.bomber.trader.dto.FeeData;
import ru.bomber.core.trader.models.Instrument;
import ru.bomber.core.trader.models.ExchangeVendor;

import java.util.List;

public interface ExchangeAPI {

    ExchangeVendor getVendor();
    FeeData getFee();

    List<Instrument> getInstruments(String instrument);
    List<Instrument> getInstruments();

    Instrument getInstrument(String instrumentName);
}

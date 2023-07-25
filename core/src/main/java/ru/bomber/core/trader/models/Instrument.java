package ru.bomber.core.trader.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Instrument {

    private String instrument;

    private String baseCurrencyName;

    private String quoteCurrencyName;

    private StateInstrument state;
}

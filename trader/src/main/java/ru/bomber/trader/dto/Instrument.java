package ru.bomber.trader.dto;

import lombok.Getter;
import lombok.Setter;
import ru.bomber.trader.models.StateInstrument;

@Setter
@Getter
public class Instrument {

    private String instrument;

    private String baseCurrencyName;

    private String quoteCurrencyName;

    private StateInstrument state;
}

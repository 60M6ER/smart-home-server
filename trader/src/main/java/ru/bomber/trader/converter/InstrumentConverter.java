package ru.bomber.trader.converter;

import com.poloniex.api.client.model.Market;
import ru.bomber.trader.dto.Instrument;
import ru.bomber.trader.models.StateInstrument;

public class InstrumentConverter {

    public static Instrument toDTO(Market market) {
        Instrument instrument = new Instrument();

        instrument.setInstrument(market.getSymbol());
        instrument.setBaseCurrencyName(market.getBaseCurrencyName());
        instrument.setQuoteCurrencyName(market.getQuoteCurrencyName());
        instrument.setState(StateInstrument.valueOf(market.getState()));

        return instrument;
    }
}
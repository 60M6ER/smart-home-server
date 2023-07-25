package ru.bomber.trader.utils;

import com.poloniex.api.client.rest.PoloRestClient;
import lombok.extern.slf4j.Slf4j;
import ru.bomber.trader.converter.FeeConverter;
import ru.bomber.trader.converter.InstrumentConverter;
import ru.bomber.trader.dto.FeeData;
import ru.bomber.core.trader.models.Instrument;
import ru.bomber.core.trader.models.ExchangeVendor;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class PoloAPI implements ExchangeAPI {

    private static final String POLO_PROD_HOST = "https://api.poloniex.com";
    private static final String POLO_TEST_HOST = "https://sandbox-api.poloniex.com";

    private final PoloRestClient restClient;

    public PoloAPI(String APIKey, String secret) {

        restClient = new PoloRestClient(POLO_PROD_HOST, APIKey, secret);
    }

    @Override
    public ExchangeVendor getVendor() {
        return ExchangeVendor.POLONIEX;
    }

    @Override
    public FeeData getFee() {
        return FeeConverter.toDTO(restClient.getFeeInfo());
    }

    @Override
    public List<Instrument> getInstruments(String instrument) {
        return restClient.getMarkets().stream()
                .filter(market -> market.getSymbol().contains(instrument))
                .map(InstrumentConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Instrument> getInstruments() {
        return restClient.getMarkets().stream()
                .map(InstrumentConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Instrument getInstrument(String instrumentName) {
        return InstrumentConverter.toDTO(restClient.getMarket(instrumentName).get(0));
    }


}

package ru.larionov.backend.services.binance;

import com.binance.connector.client.impl.SpotClientImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ru.larionov.backend.converter.FeeConverter;
import ru.larionov.backend.converter.OrderBookConverter;
import ru.larionov.backend.converter.PairCurrencyConverter;
import ru.larionov.backend.dto.exchange.binance.BinanceAccountInfo;
import ru.larionov.backend.dto.exchange.binance.BinanceExchangeInfo;
import ru.larionov.backend.dto.exchange.binance.BinanceOrderBook;
import ru.larionov.backend.model.*;
import ru.larionov.backend.model.Currency;
import ru.larionov.backend.services.ExchangeHandler;

import java.util.*;

@Slf4j
public class BinanceHandler implements ExchangeHandler {

    private final String API_KEY;
    private final String SECRET;

    private final SpotClientImpl spotClient;
    private final ObjectMapper mapper;


    public BinanceHandler(String API_KEY, String SECRET) {
        mapper = new ObjectMapper();
        this.API_KEY = API_KEY;
        this.SECRET = SECRET;

        spotClient = new SpotClientImpl(API_KEY, SECRET);
    }

    @Override
    public ExchangeVendor getVendor() {
        return ExchangeVendor.BINANCE;
    }

    @Override
    public void update() {

    }

    @Override
    public List<Currency> getPortfolio() {
        return new ArrayList<>();
    }

    @Override
    public List<PairCurrency> getPairs() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("symbolStatus", "TRADING");
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add("SPOT");
        parameters.put("permissions", permissions);
        try {
            List<PairCurrency> pairCurrencies = mapper.readValue(spotClient.createMarket().exchangeInfo(parameters),
                            BinanceExchangeInfo.class).symbols.stream()
                    .map(PairCurrencyConverter::fromBinanceSymbol)
                    .toList();
            pairCurrencies.forEach(pair -> pair.setVendor(ExchangeVendor.BINANCE));
            return pairCurrencies;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FeeInformation getFee() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("timestamp", new Date().getTime());
        parameters.put("omitZeroBalances", true);
        try {
            return FeeConverter.fromBinanceAccountInfo(
                    mapper.readValue(
                            spotClient.createTrade().account(parameters),
                            BinanceAccountInfo.class
                    )
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PricePair> getMarketPrices() {
        return null;
    }

    @Override
    public OrderBook getOrderBook(PairCurrency pairCurrency) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("symbol", pairCurrency.getExchangeToken());
        parameters.put("limit", 5);
        try {
            return OrderBookConverter.fromBinanceOrderBook(mapper.readValue(
                            spotClient.createMarket().depth(parameters),
                            BinanceOrderBook.class),
                    pairCurrency.getExchangeToken());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

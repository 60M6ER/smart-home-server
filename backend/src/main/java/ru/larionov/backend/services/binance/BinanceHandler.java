package ru.larionov.backend.services.binance;

import com.binance.connector.client.impl.SpotClientImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ru.larionov.backend.converter.*;
import ru.larionov.backend.dto.exchange.binance.*;
import ru.larionov.backend.dto.portfolio.CurrencyPermissions;
import ru.larionov.backend.model.*;
import ru.larionov.backend.model.Currency;
import ru.larionov.backend.services.ExchangeHandler;

import java.util.*;
import java.util.stream.Stream;

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
    public List<Currency> getBalances() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("recvWindow", 20000L);
        parameters.put("timestamp", new Date().getTime());
        try {
            List<Currency> currencies = Stream.of(mapper.readValue(spotClient.createWallet().getUserAsset(parameters),
                            BinanceUserAsset[].class))
                    .map(CurrencyConverter::fromBinanceAsset)
                    .toList();
            currencies.forEach(currency -> currency.setVendor(ExchangeVendor.BINANCE));
            return currencies;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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
        parameters.put("recvWindow", 20000L);
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

    @Override
    public List<PairCurrency> getMarkPrices() {
        Map<String, Object> parameters = new HashMap<>();
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add("SPOT");
        parameters.put("permissions", permissions);
        try {
            List<PairCurrency> pairCurrencies = mapper.readValue(spotClient.createMarket().exchangeInfo(parameters),
                            BinanceExchangeInfo.class).symbols.stream()
                    .map(PairCurrencyConverter::fromBinanceSymbol)
                    .toList();
            pairCurrencies.forEach(pair -> pair.setVendor(ExchangeVendor.BINANCE));
            parameters = new HashMap<>();
            BinancePriceTicker[] binancePriceTicker = mapper.readValue(spotClient.createMarket().tickerSymbol(parameters),
                    BinancePriceTicker[].class);
            Stream.of(binancePriceTicker)
                    .forEach(price -> {
                        for (PairCurrency pair: pairCurrencies) {
                            if (pair.getExchangeToken().equals(price.getSymbol())){
                                pair.setMarkPrice(price.getPrice());
                                break;
                            }
                        }
                    });
            return pairCurrencies;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CurrencyPermissions getCurrencyPermissions(String currencyToken) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("recvWindow", 20000L);
        parameters.put("timestamp", new Date().getTime());
        try {
            return Stream.of(mapper.readValue(spotClient.createWallet().coinInfo(parameters),
                            BinanceCurrencyInfo[].class))
                    .filter(c -> c.getCoin().equals(currencyToken))
                    .map(CurrencyPermissionsConverter::fromBinance)
                    .findFirst()
                    .orElseThrow();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DepositAddress getDepositAddress(String currencyToken, String networkToken) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("recvWindow", 20000L);
        parameters.put("timestamp", new Date().getTime());
        parameters.put("network", networkToken);
        try {
            BinanceDepositAddress binanceDepositAddress = mapper.readValue(spotClient.createWallet().depositAddress(parameters),
                    BinanceDepositAddress.class);
            DepositAddress depositAddress = new DepositAddress();
            depositAddress.setCurrencyToken(currencyToken);
            depositAddress.setVendor(ExchangeVendor.BINANCE);
            depositAddress.setAddress(binanceDepositAddress.getAddress());
            return depositAddress;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

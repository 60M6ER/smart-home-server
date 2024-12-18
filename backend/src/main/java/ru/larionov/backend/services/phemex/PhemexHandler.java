package ru.larionov.backend.services.phemex;

import com.phemex.client.PhemexClient;
import com.phemex.client.impl.PhemexClientBuilder;
import com.phemex.client.ws.PhemexMessageListener;
import com.poloniex.api.client.spot.model.response.spot.Market;
import com.poloniex.api.client.spot.rest.spot.SpotPoloRestClient;
import lombok.extern.slf4j.Slf4j;
import ru.larionov.backend.converter.*;
import ru.larionov.backend.dto.portfolio.CurrencyPermissions;
import ru.larionov.backend.exception.ExchangeHandlerException;
import ru.larionov.backend.model.*;
import ru.larionov.backend.services.ExchangeHandler;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PhemexHandler implements ExchangeHandler, PhemexMessageListener {

    private static final String PHEMEX_HOST_URL = "https://api.phemex.com";
    private static final String PHEMEX_WS_URL = "wss://ws.phemex.com";

    private final String API_KEY;
    private final String SECRET;

    private final PhemexClient phemexClient;

    private Long spotID;

    public PhemexHandler(String API_KEY, String SECRET) {
        this.API_KEY = API_KEY;
        this.SECRET = SECRET;

        phemexClient = PhemexClient.builder()
                .apiKey(API_KEY)
                .apiSecret(SECRET)
                .url(PHEMEX_HOST_URL)
                .connectionTimeout(Duration.ofSeconds(100))
                .expiryDuration(Duration.ofSeconds(60))
                .wsUri(PHEMEX_WS_URL)
                .messageListener(this)
                .build();
    }

    @Override
    public ExchangeVendor getVendor() {
        return ExchangeVendor.PHEMEX;
    }

    @Override
    public void update() {
        //publicWebsocketClient.
    }

    @Override
    public List<Currency> getBalances() {
//        if (spotID == null)
//            throw new ExchangeHandlerException("ID of SPOT account is not set.");
//        return poloRestClient.getAccountBalancesById(spotID)
//                .stream()
//                .flatMap(accountBalance -> accountBalance.getBalances().stream())
//                .map(CurrencyConverter::fromPoloniexCurrency)
//                .toList();
        return new ArrayList<>();
    }

    @Override
    public List<PairCurrency> getPairs() {
        //phemexClient.
//        List<PairCurrency> pairCurrencies = poloRestClient.getMarkets().stream()
//                .filter(market -> market.getState().equals("NORMAL"))
//                .map(PairCurrencyConverter::fromPoloniexMarket)
//                .toList();
//        pairCurrencies.forEach(pairCurrency -> pairCurrency.setVendor(ExchangeVendor.POLONIEX));
        return null;
    }

    @Override
    public List<PricePair> getMarketPrices() {
//        return poloRestClient.getPrices().stream()
//                .map(PricePairConverter::fromPoloniexPricePair)
//                .toList();
        return null;
    }

    @Override
    public FeeInformation getFee() {
        //phemexClient.
        //return FeeConverter.fromPoloniexFee(poloRestClient.getFeeInfo());
        return null;
    }

    @Override
    public OrderBook getOrderBook(PairCurrency pairCurrency) {
//        return OrderBookConverter.fromPoloniexOrderBook(poloRestClient.getOrderBook(pairCurrency.getToken(),
//                Double.toString(1 / Math.pow(10, pairCurrency.getPriceScale())),
//                5), pairCurrency.getToken());
        return null;

    }

    @Override
    public List<PairCurrency> getMarkPrices() {
        return new ArrayList<>();
    }

    @Override
    public CurrencyPermissions getCurrencyPermissions(String currencyToken) {
        return null;
    }

    @Override
    public void onMessage(String s) {

    }

    @Override
    public void onError(Exception e) {

    }
}

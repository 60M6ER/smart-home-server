package ru.larionov.backend.services.poloniex;

import com.poloniex.api.client.spot.model.response.spot.*;
import com.poloniex.api.client.spot.rest.spot.SpotPoloRestClient;
import lombok.extern.slf4j.Slf4j;
import ru.larionov.backend.converter.*;
import ru.larionov.backend.exception.ExchangeHandlerException;
import ru.larionov.backend.model.*;
import ru.larionov.backend.model.Currency;
import ru.larionov.backend.model.OrderBook;
import ru.larionov.backend.services.ExchangeHandler;
import java.util.List;

@Slf4j
public class PoloniexHandler implements ExchangeHandler{

    private static final String POLO_HOST_URL = "https://api.poloniex.com";
    private static final String POLO_PUBLIC_WS_URL = "wss://ws.poloniex.com/ws/public";
    private static final String POLO_PRIVATE_WS_URL = "wss://ws.poloniex.com/ws/private";

    private final String API_KEY;
    private final String SECRET;

    private final SpotPoloRestClient poloRestClient;

    private Long spotID;

    public PoloniexHandler(String API_KEY, String SECRET) {
        this.API_KEY = API_KEY;
        this.SECRET = SECRET;

        poloRestClient = new SpotPoloRestClient(POLO_HOST_URL, API_KEY, SECRET);
        List<String> symbols = poloRestClient.getMarkets().stream()
                .map(Market::getSymbol)
                .toList();
        poloRestClient.getAccounts().stream()
                .filter(a -> a.getAccountType().equals("SPOT"))
                .findFirst()
                .ifPresent(account -> spotID = Long.valueOf(account.getAccountId()));
        getFee();
    }

    @Override
    public ExchangeVendor getVendor() {
        return ExchangeVendor.POLONIEX;
    }

    @Override
    public void update() {
        //publicWebsocketClient.
    }

    @Override
    public List<Currency> getPortfolio() {
        if (spotID == null)
            throw new ExchangeHandlerException("ID of SPOT account is not set.");
        return poloRestClient.getAccountBalancesById(spotID)
                .stream()
                .flatMap(accountBalance -> accountBalance.getBalances().stream())
                .map(CurrencyConverter::fromPoloniexCurrency)
                .toList();
    }

    @Override
    public List<PairCurrency> getPairs() {
        List<PairCurrency> pairCurrencies = poloRestClient.getMarkets().stream()
                .filter(market -> market.getState().equals("NORMAL"))
                .map(PairCurrencyConverter::fromPoloniexMarket)
                .toList();
        pairCurrencies.forEach(pairCurrency -> pairCurrency.setVendor(ExchangeVendor.POLONIEX));
        return pairCurrencies;
    }

    @Override
    public List<PricePair> getMarketPrices() {
        return poloRestClient.getPrices().stream()
                .map(PricePairConverter::fromPoloniexPricePair)
                .toList();
    }

    @Override
    public FeeInformation getFee() {
        return FeeConverter.fromPoloniexFee(poloRestClient.getFeeInfo());
    }

    @Override
    public OrderBook getOrderBook(PairCurrency pairCurrency) {
        return OrderBookConverter.fromPoloniexOrderBook(poloRestClient.getOrderBook(pairCurrency.getToken(),
                Double.toString(1 / Math.pow(10, pairCurrency.getPriceScale())),
                5), pairCurrency.getToken());

    }
}

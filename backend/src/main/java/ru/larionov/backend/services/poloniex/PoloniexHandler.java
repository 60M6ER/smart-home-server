package ru.larionov.backend.services.poloniex;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poloniex.api.client.spot.model.request.spot.NewCurrencyAddressRequest;
import com.poloniex.api.client.spot.model.request.spot.NewCurrencyAddressRequest.NewCurrencyAddressRequestBuilder;
import com.poloniex.api.client.spot.model.request.spot.WithdrawCurrencyRequest;
import com.poloniex.api.client.spot.model.response.spot.*;
import com.poloniex.api.client.spot.rest.spot.SpotPoloRestClient;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import ru.larionov.backend.converter.*;
import ru.larionov.backend.dto.exchange.poloniex.PoloniexCurrencyInformation;
import ru.larionov.backend.dto.portfolio.CurrencyPermissions;
import ru.larionov.backend.exception.ExchangeHandlerException;
import ru.larionov.backend.model.*;
import ru.larionov.backend.model.Currency;
import ru.larionov.backend.model.OrderBook;
import ru.larionov.backend.services.ExchangeHandler;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.Map;

@Slf4j
public class PoloniexHandler implements ExchangeHandler{

    private static final String POLO_HOST_URL = "https://api.poloniex.com";
    private static final String POLO_PUBLIC_WS_URL = "wss://ws.poloniex.com/ws/public";
    private static final String POLO_PRIVATE_WS_URL = "wss://ws.poloniex.com/ws/private";
    private static final int TIMEOUT = 5000;

    private final String API_KEY;
    private final String SECRET;

    private final WebClient webClient;
    private final SpotPoloRestClient poloRestClient;
    private final ObjectMapper mapper;

    private Long spotID;

    public PoloniexHandler(String API_KEY, String SECRET) {
        this.API_KEY = API_KEY;
        this.SECRET = SECRET;

        mapper = new ObjectMapper();
        webClient = WebClient.builder()
                .baseUrl(POLO_HOST_URL)
                .clientConnector(
                        new ReactorClientHttpConnector(
                                HttpClient.create()
                                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT)
                                        .doOnConnected(connection -> {
                                            connection.addHandlerLast(new ReadTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS));
                                            connection.addHandlerLast(new WriteTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS));
                                        })))
                .exchangeStrategies(
                        ExchangeStrategies.builder()
                                .codecs(
                                        configurer -> configurer.defaultCodecs()
                                                .maxInMemorySize(2 * 1024 * 1024)
                                )
                                .build()
                )
                .build();

        poloRestClient = new SpotPoloRestClient(POLO_HOST_URL, API_KEY, SECRET);
        poloRestClient.getAccounts().stream()
                .filter(a -> a.getAccountType().equals("SPOT"))
                .findFirst()
                .ifPresent(account -> spotID = Long.valueOf(account.getAccountId()));
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
    public List<Currency> getBalances() {
        if (spotID == null)
            throw new ExchangeHandlerException("ID of SPOT account is not set.");
        List<Currency> currencies = poloRestClient.getAccountBalancesById(spotID)
                .stream()
                .flatMap(accountBalance -> accountBalance.getBalances().stream())
                .map(CurrencyConverter::fromPoloniexCurrency)
                .toList();
        currencies.forEach(currency -> currency.setVendor(ExchangeVendor.POLONIEX));
        return currencies;
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
    public List<PairCurrency> getMarkPrices() {
        List<PairCurrency> pairCurrencies = poloRestClient.getMarkets().stream()
                .map(PairCurrencyConverter::fromPoloniexMarket)
                .toList();
        pairCurrencies.forEach(pairCurrency -> pairCurrency.setVendor(ExchangeVendor.POLONIEX));
        poloRestClient.getMarkPrices().forEach(markPrice -> {
            for (PairCurrency pairCurrency : pairCurrencies) {
                if (pairCurrency.getExchangeToken().equals(markPrice.getSymbol())) {
                    pairCurrency.setMarkPrice(Double.parseDouble(markPrice.getMarkPrice()));
                    break;
                }
            }
        });
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

    @Override
    public CurrencyPermissions getCurrencyPermissions(String currencyToken) {
        return CurrencyPermissionsConverter.fromPoloniex(
                Objects.requireNonNull(
                        webClient.get()
                                .uri(POLO_HOST_URL + "/v2/currencies/" + currencyToken)
                                .retrieve()
                                .bodyToMono(PoloniexCurrencyInformation.class)
                                .block())
        );
    }

    @Override
    public DepositAddress getDepositAddress(String currencyToken, String networkToken) {
        Map<String, String> depositAddressesByCurrency = poloRestClient.getDepositAddressesByCurrency(currencyToken);
        DepositAddress depositAddress = new DepositAddress();
        depositAddress.setVendor(ExchangeVendor.POLONIEX);
        depositAddress.setCurrencyToken(currencyToken);
        if (depositAddressesByCurrency.size() == 0) {
            NewCurrencyAddressResponse newCurrencyAddressResponse =
                    poloRestClient.addNewCurrencyAddress(
                            NewCurrencyAddressRequest.builder()
                                    .currency(currencyToken)
                                    .build());
            depositAddress.setAddress(newCurrencyAddressResponse.getAddress());
        } else {
            depositAddress.setAddress(depositAddressesByCurrency.get(currencyToken));
        }
        return depositAddress;
    }
    
    public void createWithdrawal(String currencyToken, String networkToken, String tag, String amount) {
        WithdrawCurrencyRequest withdrawCurrencyRequest = WithdrawCurrencyRequest.builder()
                .currency(currencyToken)
                .amount(amount)
                .build();
        WithdrawCurrencyResponse withdrawCurrencyResponse = poloRestClient.withdrawCurrency(withdrawCurrencyRequest);
    }
}

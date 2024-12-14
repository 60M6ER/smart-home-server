package ru.larionov.backend.services.hitBtc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import ru.larionov.backend.converter.OrderBookConverter;
import ru.larionov.backend.converter.PairCurrencyConverter;
import ru.larionov.backend.dto.hitbtc.HitBTCOrderBook;
import ru.larionov.backend.dto.hitbtc.HitBTCSymbol;
import ru.larionov.backend.model.Currency;
import ru.larionov.backend.model.*;
import ru.larionov.backend.services.ExchangeHandler;
import java.util.concurrent.TimeUnit;

import java.util.*;
import java.util.stream.Stream;

@Slf4j
public class HitBTCHandler implements ExchangeHandler {

    private static final String REST_API_URL = "https://api.hitbtc.com/api/3";
    private static final int TIMEOUT = 5000;

    private final String API_KEY;
    private final String SECRET;


    private final WebClient spotClient;
    private final ObjectMapper mapper;


    public HitBTCHandler(String API_KEY, String SECRET) {
        mapper = new ObjectMapper();
        this.API_KEY = API_KEY;
        this.SECRET = SECRET;

        spotClient = WebClient.builder()
                .baseUrl(REST_API_URL)
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

    }

    @Override
    public ExchangeVendor getVendor() {
        return ExchangeVendor.HIT_BTC;
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
        String answer = spotClient.get()
                .uri("/public/symbol")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        if (answer != null) {
            StringBuilder sb = new StringBuilder("[{\"symbol\":" + answer.substring(1, answer.length() - 1) + "]");
            replaceInStringBuilder(sb, ":{", ",");
            replaceInStringBuilder(sb, "},\"", "},{\"symbol\":\"");
            try {
                HitBTCSymbol[] hitBTCSymbols = mapper.readValue(sb.toString(), HitBTCSymbol[].class);
                List<PairCurrency> pairCurrencies = Stream.of(hitBTCSymbols)
                        .filter(h -> h.getBase_currency() != null)
                        .map(PairCurrencyConverter::fromHiBTCSymbol)
                        .filter(PairCurrency::isMarginTrading)
                        .toList();
                pairCurrencies.forEach(pairCurrency -> pairCurrency.setVendor(ExchangeVendor.HIT_BTC));
                return pairCurrencies;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }


        return new ArrayList<>();
    }

    private void replaceInStringBuilder(StringBuilder sb, String oldString, String newString) {
        int i = sb.indexOf(oldString);
        while (i > -1) {
            sb.replace(i, i + oldString.length(), newString);
            i = sb.indexOf(oldString);
        }
    }
    @Override
    public FeeInformation getFee() {
        FeeInformation feeInformation = new FeeInformation();
        feeInformation.setTaker(0.001);
        feeInformation.setMaker(0.001);
        return feeInformation;
    }

    @Override
    public List<PricePair> getMarketPrices() {
        return null;
    }

    @Override
    public OrderBook getOrderBook(PairCurrency pairCurrency) {
        HitBTCOrderBook hitBTCOrderBooks = spotClient.get()
                .uri("/public/orderbook/" + pairCurrency.getExchangeToken())
                .retrieve()
                .bodyToMono(HitBTCOrderBook.class)
                .block();
        if (hitBTCOrderBooks != null) {
            return OrderBookConverter.fromHitBTCOrderBook(hitBTCOrderBooks, pairCurrency.getExchangeToken());

        }
        throw new RuntimeException("Orderbook wasn't");
    }
}

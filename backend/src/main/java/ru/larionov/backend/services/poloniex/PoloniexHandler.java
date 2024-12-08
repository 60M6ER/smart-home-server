package ru.larionov.backend.services.poloniex;

import com.poloniex.api.client.spot.model.response.spot.Account;
import com.poloniex.api.client.spot.model.response.spot.AccountBalance;
import com.poloniex.api.client.spot.model.response.spot.Market;
import com.poloniex.api.client.spot.rest.spot.SpotPoloRestClient;
import lombok.extern.slf4j.Slf4j;
import ru.larionov.backend.converter.CurrencyConverter;
import ru.larionov.backend.exception.ExchangeHandlerException;
import ru.larionov.backend.model.Currency;
import ru.larionov.backend.model.ExchangeVendor;
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
}

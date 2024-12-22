package ru.larionov.backend.services;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.larionov.backend.dto.portfolio.*;
import ru.larionov.backend.exception.NetworksAreNotEquals;
import ru.larionov.backend.model.Currency;
import ru.larionov.backend.model.ExchangeVendor;
import ru.larionov.backend.model.PairCurrency;
import ru.larionov.backend.model.TypeDeposit;
import ru.larionov.backend.repositories.CurrencyRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class PortfolioService {

    private static final String MAIN_CURRENCY_NAME = "USDT";

    private static final Object currencyMapMonitor = new Object();

    private final ExchangeHandlerService exchangeHandlerService;
    private final WalletService walletService;
    private final CurrencyRepository currencyRepository;
    private final TelegramService telegramService;
    private final WebClient webClient;

    private HashMap<CurrencyKey, Currency> currencyHashMap = new HashMap<>();
    private List<PairCurrency> markPrices;

    private double usdBalance;
    private double rubBalance;
    private double usd_rub;

    @PostConstruct
    private void startLogic() {
        updateCurrencies();
    }

    private HashMap<CurrencyKey, Currency> getCurrencyHashMap()  {
        synchronized (currencyMapMonitor) {
            return currencyHashMap;
        }
    }

    private void setCurrencyHashMap(HashMap<CurrencyKey, Currency> currencyHashMap)  {
        synchronized (currencyMapMonitor) {
            this.currencyHashMap = currencyHashMap;
        }
    }

    @Transactional
    public void updateCurrencies() {
        getDataFromCBRF();
        log.info("Start updating balances");
        markPrices = exchangeHandlerService.getMarkPrices();
        usdBalance = 0;
        HashMap<CurrencyKey, Currency> newCurrencyMap = new HashMap<>();

        List<Currency> currencies = exchangeHandlerService.getBalances();

        currencies.forEach(currency -> {
            Optional<Currency> optional =
                    currencyRepository.findByNameAndVendor(
                            currency.getName(),
                            currency.getVendor()
                    );
            Currency savedCurrency;
            if (optional.isPresent()) {
                savedCurrency = optional.get();
                savedCurrency.setAmount(currency.getAmount());
                savedCurrency.setHoldAmount(currency.getHoldAmount());
            } else {
                currency.setId(UUID.randomUUID());
                savedCurrency = currencyRepository.save(currency);
            }
            if (savedCurrency.getName().equals(MAIN_CURRENCY_NAME)){
                savedCurrency.setUsdEqual(savedCurrency.getAmount() + savedCurrency.getHoldAmount());
            } else {
                markPrices.stream()
                        .filter(pairCurrency ->
                                pairCurrency.getBaseCurrency().equals(savedCurrency.getName()))
                        .findFirst()
                        .ifPresent(pairCurrency ->
                                savedCurrency.setUsdEqual(
                                        (savedCurrency.getAmount() + savedCurrency.getHoldAmount()) * pairCurrency.getMarkPrice()
                                ));
            }
            usdBalance += savedCurrency.getUsdEqual();
            currencyRepository.save(savedCurrency);
            newCurrencyMap.put(new CurrencyKey(savedCurrency.getName(), savedCurrency.getVendor()), savedCurrency);
        });
        setCurrencyHashMap(newCurrencyMap);
        rubBalance = usdBalance * usd_rub;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void getDataFromCBRF () {
        Mono<String> cbrfResponseMono = webClient.get()
                .uri("https://www.cbr-xml-daily.ru/latest.js")
                .retrieve()
                .bodyToMono(String.class);

        cbrfResponseMono.subscribe(this::updateUSD);
    }

    public void updateUSD (String cbrfResponse) {
        String findingChain = "\"USD\": ";
        int start = cbrfResponse.indexOf(findingChain);
        int end = cbrfResponse.indexOf(",", start);
        this.usd_rub = 1 / Double.parseDouble(cbrfResponse.substring(start + findingChain.length(), end));
        log.info(String.format("USD-RUB: %.2f рубля", this.usd_rub));
    }

    public ViewPortfolio getViewPortfolio() {
        ViewPortfolio viewPortfolio = new ViewPortfolio();
        viewPortfolio.setUsdAmount(usdBalance);
        viewPortfolio.setRubAmount(rubBalance);
        return viewPortfolio;
    }

    public void updateBalances() {
        updateCurrencies();
    }

    public List<ExchangeBalance> getExchangeBalances() {
        Map<ExchangeVendor, Double> balances = new HashMap<>();
        synchronized (currencyMapMonitor) {
            currencyHashMap.forEach(((currencyKey, currency) -> {
                if (balances.containsKey(currencyKey.getVendor())) {
                    balances.put(currencyKey.getVendor(),
                            balances.get(currencyKey.getVendor()) +
                                    currency.getUsdEqual());
                }else {
                    balances.put(currencyKey.getVendor(), currency.getUsdEqual());
                }
            }));
        }
        List<ExchangeBalance> result = new ArrayList<>();
        balances.forEach((vendor, aDouble) -> result.add(new ExchangeBalance(ExchangeVendor.getView(vendor), aDouble)));
        return result;
    }

    public double getFeeEqualizeBalances () throws NetworksAreNotEquals {
        String currencyToken = "USDT";
        CurrencyPermissions srcCurrencyPermissions = walletService.getCurrencyPermissions(currencyToken, ExchangeVendor.POLONIEX);
        CurrencyPermissions targetCurrencyPermissions = walletService.getCurrencyPermissions(currencyToken, ExchangeVendor.BINANCE);

        return walletService.getFeeWithdrawal(srcCurrencyPermissions, targetCurrencyPermissions);
    }

    public String equalizeBalances() {
        String currencyToken = "USDT";


        return "OK";
    }

}

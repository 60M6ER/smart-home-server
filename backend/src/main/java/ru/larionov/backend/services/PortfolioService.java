package ru.larionov.backend.services;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.larionov.backend.dto.CBRF.CbrfResponse;
import ru.larionov.backend.dto.CBRF.USD;
import ru.larionov.backend.model.Currency;
import ru.larionov.backend.repositories.CurrencyRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class PortfolioService {

    private final ExchangeHandlerService exchangeHandlerService;
    private final CurrencyRepository currencyRepository;
    private final WebClient webClient;


    private double usd;

    @PostConstruct
    private void startLogic() {
        getDataFromCBRF();
        updateCurrencies();
    }

    @Transactional
    public void updateCurrencies() {
        log.info("Start updating balances");

        List<Currency> currencies = exchangeHandlerService.getPortfolio();

        currencies.forEach(currency -> {
            Optional<Currency> optional =
                    currencyRepository.findByNameAndVendor(
                            currency.getName(),
                            currency.getVendor()
                    );
            if (optional.isPresent()) {
                Currency savedCurrency = optional.get();
                savedCurrency.setAmount(currency.getAmount());
            } else {
                currency.setId(UUID.randomUUID());
                currencyRepository.save(currency);
            }
        });

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
        this.usd = 1 / Double.parseDouble(cbrfResponse.substring(start + findingChain.length(), end));
        log.info(String.format("USD-RUB: %.2f рубля", this.usd));
    }

}

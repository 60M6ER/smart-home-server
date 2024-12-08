package ru.larionov.backend.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.larionov.backend.model.Currency;
import ru.larionov.backend.model.Exchange;
import ru.larionov.backend.model.PairCurrency;
import ru.larionov.backend.repositories.ExchangeRepository;
import ru.larionov.backend.services.poloniex.PoloniexHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class ExchangeHandlerService {

    private final ExchangeRepository exchangeRepository;

    private List<ExchangeHandler> exchangeHandlers;

    @PostConstruct
    public void startLogic() {
        exchangeHandlers = new ArrayList<>();

        List<Exchange> allByActive = exchangeRepository.findAllByActive(true);
        allByActive.forEach(this::getExchangeHandler);
    }

    private ExchangeHandler getExchangeHandler(Exchange exchange) {
        Optional<ExchangeHandler> exchangeFromList = exchangeHandlers.stream()
                .filter(exchangeHandler -> exchangeHandler.getVendor().equals(exchange.getVendor()))
                .findFirst();
        if (exchangeFromList.isPresent())
            return exchangeFromList.get();

        ExchangeHandler handler = switch (exchange.getVendor()) {
            case POLONIEX -> new PoloniexHandler(exchange.getAPI_KEY(), exchange.getSECRET());
            default -> null;
        };
        exchangeHandlers.add(handler);
        return handler;
    }

    @Scheduled(fixedDelay = 10000)
    public void update() {
        exchangeHandlers.forEach(ExchangeHandler::update);
    }

    public List<Currency> getPortfolio() {
        return exchangeHandlers.stream()
                .flatMap(eh -> eh.getPortfolio().stream())
                .toList();
    }

    public List<PairCurrency> getPairs() {
        return exchangeHandlers.stream()
                .flatMap(eh -> eh.getPairs().stream())
                .toList();
    }
}

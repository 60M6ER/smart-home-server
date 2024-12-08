package ru.larionov.backend.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.larionov.backend.model.ChainPairs;
import ru.larionov.backend.model.PairCurrency;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpreadScanner {

    private static final String BASE_CURRENCY = "USDT";

    private final ExchangeHandlerService exchangeHandlerService;
    private List<ChainPairs> chains;

    @PostConstruct
    public void startLogic() {
        chains = new ArrayList<>();
        List<PairCurrency> pairs = exchangeHandlerService.getPairs();
        pairs
                .forEach(pairCurrency -> {
                    if (pairCurrency.getQuoteCurrency().equals(BASE_CURRENCY)) {
                        ChainPairs chain = new ChainPairs(pairCurrency, pairCurrency.getQuoteCurrency());
                        chain.scanChildren(pairs);
                        chains.add(chain);
                    }
                });
        chains = chains.stream().filter(c -> c.getCountLevels() >= 3).toList();
        chains.forEach(chainPairs ->
                chainPairs.getYoungestChildren().forEach(cp ->
                        log.info("Chain: " + cp.getParentsString())));
        log.info("count: " + chains.size());
    }
}

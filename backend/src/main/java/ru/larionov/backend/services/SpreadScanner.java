package ru.larionov.backend.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.larionov.backend.model.*;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class SpreadScanner {

    private static final String BASE_CURRENCY = "USDT";

    private final ExchangeHandlerService exchangeHandlerService;
    private final TelegramService telegramService;
    private List<ChainPairs> chains;
    private Map<ExchangeVendor, FeeInformation> fees;
    private Map<ExchangeVendor, List<PricePair>> prices;

    private int currIndex;

    @PostConstruct
    public void startLogic() {
        chains = new ArrayList<>();
        fees = new HashMap<>();
        prices = new HashMap<>();
        currIndex = -1;
    }

    @Scheduled(fixedRate = 1000 * 60 * 60)
    public void updateChains() {
        chains = new ArrayList<>();
        currIndex = -1;
        List<PairCurrency> pairs = exchangeHandlerService.getPairs();
        pairs
                .forEach(pairCurrency -> {
                    if (pairCurrency.getQuoteCurrency().equals(BASE_CURRENCY)) {
                        ChainPairs chain = new ChainPairs(pairCurrency, pairCurrency.getQuoteCurrency());
                        chain.scanChildren(pairs);
                        chains.add(chain);
                    }
                });
        chains = chains.stream()
                .flatMap(chainPairs -> chainPairs.getYoungestChildren().stream())
                .filter(chainPairs -> chainPairs.getLevel() == 2)
                .toList();
        log.info("Chain count: " + chains.size());
    }

    private FeeInformation getFee(ExchangeVendor vendor) {
        if (!fees.containsKey(vendor))
            fees.put(vendor, exchangeHandlerService.getFeeInformation(vendor));
        return fees.get(vendor);
    }

    private PricePair getPrice(ExchangeVendor vendor, String pairToken) {
        if (!prices.containsKey(vendor))
            prices.put(vendor, exchangeHandlerService.getPricesPairs(vendor));
        return prices.get(vendor).stream()
                .filter(pricePair -> pricePair.getPairToken().equals(pairToken))
                .findFirst()
                .orElse(null);
    }

    @Scheduled(fixedRate = 100)
    public void scanSpreads() {
        if (chains.size() > 0) {
            currIndex++;
            if (currIndex + 1 > chains.size())
                currIndex = 0;
            double startAmount = 100;
            double endAmount = calculateOperationAmount(startAmount,
                    chains.get(currIndex).getParentChain().getParentChain());
            endAmount = calculateOperationAmount(endAmount,
                    chains.get(currIndex).getParentChain());
            endAmount = calculateOperationAmount(endAmount,
                    chains.get(currIndex));
            if (endAmount > startAmount) {
                String incomeMessage = String.format("Доход составит: %.5f процентов",
                        (endAmount - startAmount) * 100 / startAmount);
                log.info(chains.get(currIndex).getParentsString());
                log.info(incomeMessage);
                telegramService.sendNotification(
                        "Найдена доходная цепочка:\n"
                                + chains.get(currIndex).getParentsString() + "\n"
                                + incomeMessage);
            }
        }
    }

    private double calculateOperationAmount(double amountBaseCurrency, ChainPairs chain) {
        FeeInformation feeInformation = getFee(chain.getBasePair().getVendor());
        OrderBook orderBook = exchangeHandlerService.getOrderBook(chain.getBasePair().getVendor(), chain.getBasePair());
        if (orderBook != null && feeInformation != null) {
            double a;
            TypeOrder typeOrder = chain.getBaseCurrency().equals(chain.getBasePair().getQuoteCurrency()) ?
                    TypeOrder.BUY :
                    TypeOrder.SELL;
            if (typeOrder == TypeOrder.BUY){
                // Покупаем
                a = amountBaseCurrency / orderBook.getBestPriceByTypeOrder(typeOrder, 0).getPrice();
            } else {
                // Продаем
                a = amountBaseCurrency * orderBook.getBestPriceByTypeOrder(typeOrder, 0).getPrice();
            }
            return a - (a * feeInformation.getTaker());
        } else {
            return 0;
        }
    }
}

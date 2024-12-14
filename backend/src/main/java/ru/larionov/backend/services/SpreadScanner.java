package ru.larionov.backend.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.larionov.backend.model.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class SpreadScanner {

    private static final String BASE_CURRENCY = "USDT";

    private final ExchangeHandlerService exchangeHandlerService;
    private final TelegramService telegramService;
    private Map<ExchangeVendor, List<ChainPairs>> chains;

    private List<ChainPairs> betweenChains;

    private Map<ExchangeVendor, FeeInformation> fees;
    private Map<ExchangeVendor, List<PricePair>> prices;

    private final Object chainsMonitor = new Object();

    Map<ExchangeVendor, Integer> curIndexes;

    private int currIndex;

    @PostConstruct
    public void startLogic() {
        betweenChains = new ArrayList<>();
        curIndexes = new HashMap<>();
        chains = new HashMap<>();
        fees = new HashMap<>();
        prices = new HashMap<>();
        currIndex = -1;
    }

    public void updateChainsOld() {
        List<PairCurrency> pairs = exchangeHandlerService.getPairs();
        Set<ExchangeVendor> vendors = pairs.stream()
                .map(PairCurrency::getVendor)
                .collect(Collectors.toSet());
        vendors.forEach(vendor -> {
            ArrayList<ChainPairs> chainPairs = new ArrayList<>();
            List<PairCurrency> pairCurrencies = pairs.stream()
                    .filter(pairCurrency -> pairCurrency.getVendor() == vendor)
                    .toList();
            pairCurrencies.forEach(pairCurrency -> {
                if (pairCurrency.getQuoteCurrency().equals(BASE_CURRENCY)) {
                    ChainPairs chain = new ChainPairs(pairCurrency, pairCurrency.getQuoteCurrency());
                    chain.setTypeOrder(chain.getBaseCurrency().equals(chain.getBasePair().getQuoteCurrency()) ?
                            TypeOrder.BUY :
                            TypeOrder.SELL);
                    chain.scanChildren(pairCurrencies);

                    chainPairs.add(chain);
                }
            });
            synchronized (chainsMonitor) {
                chains.put(vendor, chainPairs.stream()
                        .flatMap(cp -> cp.getYoungestChildren().stream())
                        .filter(cp -> cp.getLevel() == 2)
                        .toList());
            }
        });
        AtomicReference<Integer> countChains = new AtomicReference<>(0);
        chains.forEach((vendor, chainPairs) -> countChains.updateAndGet(v -> v + chainPairs.size()));
        log.info("Chain count: " + countChains.get());
    }
    @Scheduled(fixedRate = 1000 * 60 * 60)
    public void updateChains() {
        synchronized (chainsMonitor) {
            betweenChains.clear();
            List<PairCurrency> pairs = exchangeHandlerService.getPairs().stream()
                    .filter(PairCurrency::isMarginTrading)
                    .toList();
            pairs.stream()
                    .filter(pair -> pair.getQuoteCurrency().equals(BASE_CURRENCY))
                    .forEach(pairCurrency -> {
                        ChainPairs chain = new ChainPairs(pairCurrency, pairCurrency.getQuoteCurrency());
                        chain.setBetween(true);
                        chain.setTypeOrder(chain.getBaseCurrency().equals(chain.getBasePair().getQuoteCurrency()) ?
                                TypeOrder.BUY :
                                TypeOrder.SELL);
                        chain.scanChildren(pairs);

                        betweenChains.add(chain);
                    });
            betweenChains = betweenChains.stream()
                    .flatMap(cp -> cp.getYoungestChildren().stream())
                    .filter(cp -> cp.getLevel() == 1)
                    .toList();
            log.info("Chain count: " + betweenChains.size());
        }

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
        synchronized (chainsMonitor) {
            currIndex++;
            if (currIndex + 1 > betweenChains.size())
                currIndex = 0;

            Spread spread = new Spread(TypeSpread.BETWEEN_EXCHANGES);
            ChainPairs chain = betweenChains.get(currIndex).getParentChain();
            spread.setFeeInformation(getFee(chain.getBasePair().getVendor()));
            spread.addChain(chain,
                    exchangeHandlerService.getOrderBook(chain.getBasePair().getVendor(), chain.getBasePair()));
            chain = betweenChains.get(currIndex);
            spread.addChain(chain,
                    exchangeHandlerService.getOrderBook(chain.getBasePair().getVendor(), chain.getBasePair()));

            if (spread.getUSDT_amount_end() > spread.getUSDT_amount_start()) {
                log.info("Найдена доходная цепочка:\n" + spread);
                telegramService.sendNotification(
                        "Найдена доходная цепочка:\n"
                                + spread);
            }
        }
    }

    public void scanSpreadsOld() {
        synchronized (chainsMonitor) {
            chains.forEach((vendor, chainPairs) -> {
                if (chainPairs.size() > 0) {
                    Integer i;
                    if (curIndexes.containsKey(vendor)) {
                        i = curIndexes.get(vendor);
                        i++;
                    } else {
                        i = 0;
                    }
                    if (i + 1 > chainPairs.size())
                        i = 0;
                    curIndexes.put(vendor, i);

                    Spread spread = new Spread(TypeSpread.TRIANGLE);
                    ChainPairs chain = chainPairs.get(i).getParentChain().getParentChain();
                    spread.setFeeInformation(getFee(vendor));
                    spread.addChain(chain,
                            exchangeHandlerService.getOrderBook(vendor, chain.getBasePair()));
                    chain = chainPairs.get(i).getParentChain();
                    spread.addChain(chain,
                            exchangeHandlerService.getOrderBook(vendor, chain.getBasePair()));
                    chain = chainPairs.get(i);
                    spread.addChain(chain,
                            exchangeHandlerService.getOrderBook(vendor, chain.getBasePair()));
                    if (spread.getUSDT_amount_end() > spread.getUSDT_amount_start()) {
                        log.info("Найдена доходная цепочка:\n" + spread);
                        telegramService.sendNotification(
                                "Найдена доходная цепочка:\n"
                                        + spread);
                    }
                }
            });
        }
    }

    private double calculateOperationAmount(double amountBaseCurrency, ChainPairs chain) {
        FeeInformation feeInformation = getFee(chain.getBasePair().getVendor());
        OrderBook orderBook = exchangeHandlerService.getOrderBook(chain.getBasePair().getVendor(), chain.getBasePair());
        if (orderBook != null && feeInformation != null) {
            double a;
            if (chain.getTypeOrder() == TypeOrder.BUY){
                // Покупаем
                a = amountBaseCurrency / orderBook.getBestPriceByTypeOrder(chain.getTypeOrder(), 0).getPrice();
            } else {
                // Продаем
                a = amountBaseCurrency * orderBook.getBestPriceByTypeOrder(chain.getTypeOrder(), 0).getPrice();
            }
            return a - (a * feeInformation.getTaker());
        } else {
            return 0;
        }
    }
}

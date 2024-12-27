package ru.larionov.backend.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.larionov.backend.converter.SpreadConverter;
import ru.larionov.backend.dto.spreads.SpreadDTO;
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


    private static final int SPREADS_BUFFER = 100;

    private final ExchangeHandlerService exchangeHandlerService;
    private final TelegramService telegramService;
    private Map<ExchangeVendor, List<ChainPairs>> chains;

    private LinkedList<Spread> spreads;

    private List<ChainPairs> betweenChains;

    private Map<ExchangeVendor, FeeInformation> fees;
    private Map<ExchangeVendor, List<PricePair>> prices;

    private final Object chainsMonitor = new Object();
    private final Object spreadsMonitor = new Object();

    Map<ExchangeVendor, Integer> curIndexes;

    private int currIndex;

    @PostConstruct
    public void startLogic() {
        spreads = new LinkedList<>();
        betweenChains = new ArrayList<>();
        curIndexes = new HashMap<>();
        chains = new HashMap<>();
        fees = new HashMap<>();
        prices = new HashMap<>();
        currIndex = -1;
    }

    private void addSpread(Spread spread) {
        synchronized (spreadsMonitor) {
            Optional<Spread> activeSpread = spreads.stream()
                    .filter(s -> s.getState() == SpreadState.WORKING &&
                            s.equals(spread))
                    .findFirst();

            if (activeSpread.isPresent()) {
                activeSpread.get().updateWithAnother(spread);
            } else {
                if (spread.getUSDT_amount_end() > spread.getUSDT_amount_start()) {
                    spread.setId(UUID.randomUUID());
                    spread.setState(SpreadState.WORKING);
                    spreads.addLast(spread);
                }
            }
            if (spreads.size() > SPREADS_BUFFER) {
                spreads.removeFirst();
            }
        }
    }

    public List<SpreadDTO> getSpreadsStack() {
        List<SpreadDTO> spreadDTOS = new ArrayList<>();
        synchronized (spreadsMonitor) {
            Iterator<Spread> spreadIterator = spreads.descendingIterator();
            while (spreadIterator.hasNext()) {
                spreadDTOS.add(SpreadConverter.toDto(spreadIterator.next()));
            }
        }
        return spreadDTOS;
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
            if (betweenChains.size() == 0) return;
            currIndex++;
            if (currIndex + 1 > betweenChains.size())
                currIndex = 0;
            ChainPairs chain = betweenChains.get(currIndex);
            Spread spread = new Spread(TypeSpread.BETWEEN_EXCHANGES);
            ChainPairs pChain = chain.getParentChain();
            spread.setFeeInformation(getFee(pChain.getBasePair().getVendor()));
            spread.addChain(pChain,
                    exchangeHandlerService.getOrderBook(pChain.getBasePair().getVendor(), pChain.getBasePair()));
            spread.addChain(chain,
                    exchangeHandlerService.getOrderBook(chain.getBasePair().getVendor(), chain.getBasePair()));

            addSpread(spread);

//            if (spread.getUSDT_amount_end() > spread.getUSDT_amount_start()) {
//                log.info("Найдена доходная цепочка:\n" + spread);
//                telegramService.sendNotification(
//                        "Найдена доходная цепочка:\n"
//                                + spread);
//            }
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

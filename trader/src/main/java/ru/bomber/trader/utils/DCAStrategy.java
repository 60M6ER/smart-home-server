package ru.bomber.trader.utils;

import lombok.extern.slf4j.Slf4j;
import ru.bomber.core.trader.models.StateInstrument;
import ru.bomber.trader.dto.OrderBookDTO;
import ru.bomber.trader.models.*;
import ru.bomber.trader.reposytory.BalanceOperationRepository;
import ru.bomber.trader.reposytory.BalanceRepository;
import ru.bomber.trader.reposytory.BotIterationRepository;
import ru.bomber.trader.services.TimerService;
import ru.bomber.trader.utils.timer.TimerListener;
import ru.bomber.trader.view.Balance;

import java.util.Optional;
import java.util.UUID;


@Slf4j
public class DCAStrategy extends BaseStrategy implements TimerListener {

    private static final long START_ORDER_DELAY = 1000 * 60 * 5; //5 minutes

    public DCAStrategy(Bot bot,
                       ExchangeAPI exchangeAPI,
                       BotIterationRepository botIterationRepository,
                       BalanceRepository balanceRepository,
                       BalanceOperationRepository balanceOperationRepository,
                       TimerService timerService) {
        super(bot,
                exchangeAPI,
                botIterationRepository,
                balanceRepository,
                balanceOperationRepository,
                timerService);
    }

    @Override
    public void start() {
        fillCurIteration();
        getBalances();
        exchangeAPI.getFee();
        if (!getInstrument().getState().equals(StateInstrument.NORMAL))
            throw new RuntimeException("Pair is not NORMAL status.");
        exchangeAPI.getOrderBook(bot.getPair());
        //createStartOrder();
        timerService.addListener(this, "StartOrder", 2000);
    }

    @Override
    public void stop() {

    }

    private void fillCurIteration() {
        if (curIteration == null) {
            Optional<BotIteration> iterationById = botIterationRepository.findById(bot.getId());
            if (iterationById.isPresent()) {
                curIteration = iterationById.get();
            } else {
                createIteration();
            }
        }
        log.info("My iteration: " + curIteration.getIterationId());
    }

    private void getBalances() {
        Optional<Balance> base = balanceRepository.findByIdAndInstrument(bot.getId(), getInstrument().getBaseCurrencyName());
        baseBalance = base.isPresent() ? base.get().getAmount() : 0;
        Optional<Balance> quote = balanceRepository.findByIdAndInstrument(bot.getId(), getInstrument().getQuoteCurrencyName());
        quoteBalance = quote.isPresent() ? quote.get().getAmount() : 0;
        log.info(String.format("My balances:\n %s - %.8f\n %s - %.8f",
                getInstrument().getBaseCurrencyName(),
                baseBalance,
                getInstrument().getQuoteCurrencyName(),
                quoteBalance));
    }

    private void createStartOrder() {
        if (baseBalance.equals(0.0))
            throw new RuntimeException("Balance is 0. Continue is not allowed.");
        OrderBookDTO orderBook = exchangeAPI.getOrderBook(bot.getPair());
        Order order = new Order();
        order.setPair(bot.getPair());
        order.setRoleOrder(RoleOrder.START);
        order.setTypeOrder(TypeOrder.LIMIT);
        order.setOperation(OrderOperation.BUY);
        order.setQuantity(bot.getStepAtPercent() ?
                baseBalance * bot.getStepToBay() / 100 :
                bot.getStepToBay());
        order.setPrice(orderBook.getAsk().get(0).getPrice());//First ask in order book
        order.setAmount(order.getQuantity() * order.getPrice());
        timerService.addListener(this, "StartOrder", 2000);
    }

    @Override
    public void onEventTimer(String marker) {
        if (marker.equals("StartOrder")) {
            log.info("Start order listener was listened");
            timerService.delListener(getId());
        }
    }
}

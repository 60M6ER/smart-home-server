package ru.bomber.trader.utils;

import lombok.extern.slf4j.Slf4j;
import ru.bomber.core.trader.models.StateInstrument;
import ru.bomber.trader.models.*;
import ru.bomber.trader.reposytory.BalanceOperationRepository;
import ru.bomber.trader.reposytory.BalanceRepository;
import ru.bomber.trader.reposytory.BotIterationRepository;
import ru.bomber.trader.view.Balance;

import java.util.Optional;
import java.util.UUID;


@Slf4j
public class DCAStrategy extends BaseStrategy {


    public DCAStrategy(Bot bot,
                       ExchangeAPI exchangeAPI,
                       BotIterationRepository botIterationRepository,
                       BalanceRepository balanceRepository,
                       BalanceOperationRepository balanceOperationRepository) {
        super(bot,
                exchangeAPI,
                botIterationRepository,
                balanceRepository,
                balanceOperationRepository);
    }

    @Override
    public void start() {
        fillCurIteration();
        getBalances();
        exchangeAPI.getFee();
        if (!getInstrument().getState().equals(StateInstrument.NORMAL))
            throw new RuntimeException("Pair is not NORMAL status.");
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
        Order order = new Order();
        order.setPair(bot.getPair());
        order.setRoleOrder(RoleOrder.START);
        order.setTypeOrder(TypeOrder.MARKET);
        order.setOperation(OrderOperation.BUY);
        order.setAmount(bot.getStepAtPercent() ?
                baseBalance * bot.getStepToBay() / 100 :
                bot.getStepToBay());
    }
}

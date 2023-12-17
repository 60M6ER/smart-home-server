package ru.bomber.trader.utils;

import lombok.RequiredArgsConstructor;
import ru.bomber.core.trader.models.Instrument;
import ru.bomber.trader.models.Bot;
import ru.bomber.trader.models.BotIteration;
import ru.bomber.trader.reposytory.BalanceOperationRepository;
import ru.bomber.trader.reposytory.BalanceRepository;
import ru.bomber.trader.reposytory.BotIterationRepository;
import ru.bomber.trader.services.TimerService;

import java.util.UUID;

@RequiredArgsConstructor
public abstract class BaseStrategy implements Strategy {

    protected final Bot bot;
    protected final ExchangeAPI exchangeAPI;
    protected final BotIterationRepository botIterationRepository;
    protected final BalanceRepository balanceRepository;
    protected final BalanceOperationRepository balanceOperationRepository;
    protected final TimerService timerService;

    protected BotIteration curIteration;
    protected Double baseBalance;
    protected Double quoteBalance;

    private Instrument instrument;

    @Override
    public UUID getId() {
        return bot.getId();
    }

    protected Instrument getInstrument() {
        if (instrument == null) {
            instrument = exchangeAPI.getInstrument(bot.getPair());
        }
        return instrument;
    }

    protected void createIteration() {
        BotIteration iteration = new BotIteration(bot.getId(), UUID.randomUUID());
        botIterationRepository.save(iteration);
        curIteration = iteration;
    }
}

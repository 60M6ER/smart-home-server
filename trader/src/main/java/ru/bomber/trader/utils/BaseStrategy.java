package ru.bomber.trader.utils;

import lombok.RequiredArgsConstructor;
import ru.bomber.trader.models.Bot;
import ru.bomber.trader.models.BotIteration;
import ru.bomber.trader.reposytory.BalanceOperationRepository;
import ru.bomber.trader.reposytory.BalanceRepository;
import ru.bomber.trader.reposytory.BotIterationRepository;

import java.util.UUID;

@RequiredArgsConstructor
public abstract class BaseStrategy implements Strategy {

    protected final Bot bot;
    protected final ExchangeAPI exchangeAPI;
    protected final BotIterationRepository botIterationRepository;
    protected final BalanceRepository balanceRepository;
    protected final BalanceOperationRepository balanceOperationRepository;

    protected BotIteration curIteration;

    @Override
    public UUID getId() {
        return bot.getId();
    }
}

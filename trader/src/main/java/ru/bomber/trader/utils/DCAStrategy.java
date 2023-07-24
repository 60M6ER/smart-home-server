package ru.bomber.trader.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.bomber.trader.models.Bot;
import ru.bomber.trader.models.BotIteration;
import ru.bomber.trader.reposytory.BalanceOperationRepository;
import ru.bomber.trader.reposytory.BalanceRepository;
import ru.bomber.trader.reposytory.BotIterationRepository;

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
        exchangeAPI.getFee();
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
                BotIteration iteration = new BotIteration(bot.getId(), UUID.randomUUID());
                botIterationRepository.save(iteration);
                curIteration = iteration;
            }
        }
    }
}

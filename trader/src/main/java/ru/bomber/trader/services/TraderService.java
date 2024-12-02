package ru.bomber.trader.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.bomber.core.mqtt.MqttService;
import ru.bomber.core.mqtt.Topics;
import ru.bomber.trader.models.Bot;
import ru.bomber.core.trader.models.ExchangeVendor;
import ru.bomber.trader.reposytory.BalanceOperationRepository;
import ru.bomber.trader.reposytory.BalanceRepository;
import ru.bomber.trader.reposytory.BotIterationRepository;
import ru.bomber.trader.reposytory.BotRepository;
import ru.bomber.trader.utils.*;
import ru.bomber.trader.utils.timer.TimerListener;
import ru.bomber.trader.utils.timer.TimerProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TraderService {

    private static final String START_MESSAGE = "Сервис торговли был запущен";

    private final BotRepository botRepository;
    private final MqttService mqttService;
    private final BotIterationRepository botIterationRepository;
    private final BalanceRepository balanceRepository;
    private final BalanceOperationRepository balanceOperationRepository;

    private final TimerService timerService;

    @Value("${trader.api_key}")
    private String poloAPIKey;
    @Value("${trader.secret_key}")
    private String poloSecret;

    private List<Strategy> bots;
    private List<ExchangeAPI> apis;

    @PostConstruct
    private void init() throws MqttException {
        bots = new ArrayList<>();
        apis = new ArrayList<>();
        mqttService.sendMessage(Topics.TRADER_LOG, DateWriter.getMessage(START_MESSAGE), 2);
        log.info(START_MESSAGE);
    }

    @Scheduled(fixedDelay = 10000)
    private void startStopBots() {
        List<Bot> botList = botRepository.findAll();
        botList.forEach(bot -> {
            if (bot.getActive()) {
                startBot(bot);
            }else {
                stopBot(bot);
            }
        });
    }

    private void startBot(Bot bot) {
        if (bots.stream().noneMatch(b -> b.getId().equals(bot.getId()))) {
            try {
                ExchangeAPI api = getAPI(bot.getVendor());

                Strategy strategy = createStrategy(bot, api);
                strategy.start();
                bots.add(strategy);

                String mes = "Бот %s [%s] успешано стартовал.";
                mes = String.format(mes,
                        bot.getName(),
                        bot.getId().toString());
                mqttService.sendMessage(Topics.TRADER_LOG, DateWriter.getMessage(mes), 2);
            } catch (Exception e) {
                log.error("Error was during starting bot.", e);
                String ermes = "При запуске бота %s [%s] возникла ошибка: %s";
                ermes = String.format(ermes,
                        bot.getName(),
                        bot.getId().toString(),
                        e.getMessage());
                bot.setActive(false);
                botRepository.save(bot);
                try {
                    mqttService.sendMessage(Topics.TRADER_LOG, DateWriter.getMessage(ermes), 2);
                } catch (MqttException ex) {
                    log.error("Error was during starting bot.", e);
                }
            }
        }
    }

    private void stopBot(Bot bot) {
        Optional<Strategy> strategy = bots.stream().filter(b -> b.getId().equals(bot.getId())).findFirst();
        strategy.ifPresent(value -> {
            bots.remove(value);
            value.stop();
            String mes = "Bot %s [%s] was stop.";
            mes = String.format(mes,
                    bot.getName(),
                    bot.getId().toString());
            log.info(mes);
            try {
                mqttService.sendMessage(Topics.TRADER_LOG, DateWriter.getMessage(mes), 2);
            } catch (MqttException e) {
                log.error("Error was during stopping bot.", e);
            }
        });
    }

    public ExchangeAPI getAPI(ExchangeVendor vendor) {
        Optional<ExchangeAPI> exchangeAPI = apis.stream().filter(api -> api.getVendor().equals(vendor)).findFirst();
        if (exchangeAPI.isPresent()) {
            return exchangeAPI.get();
        } else {
            ExchangeAPI newAPI = createExchangeAPI(vendor);
            apis.add(newAPI);
            return newAPI;
        }
    }

    private ExchangeAPI createExchangeAPI(ExchangeVendor vendor) {
        switch (vendor) {
            case TINKOFF:
                return new TinkoffAPI();
            case POLONIEX:
                return new PoloAPI(poloAPIKey, poloSecret);
        }
        return null;
    }

    private Strategy createStrategy(Bot bot, ExchangeAPI exchangeAPI) {
        switch (bot.getStrategyType()) {
            case DCA:
                return new DCAStrategy(bot,
                        exchangeAPI,
                        botIterationRepository,
                        balanceRepository,
                        balanceOperationRepository,
                        timerService);
        }
        return null;
    }
}

package ru.bomber.trader.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Service;
import ru.bomber.core.mqtt.MqttService;
import ru.bomber.core.mqtt.Topics;

@Service
@RequiredArgsConstructor
@Slf4j
public class TraderService {

    private static final String START_MESSAGE = "Сервис торговли был запущен";

    private final MqttService mqttService;

    @PostConstruct
    private void init() throws MqttException {
        mqttService.sendMessage(Topics.TRADER_LOG, START_MESSAGE, 2);
        log.info(START_MESSAGE);
    }


}

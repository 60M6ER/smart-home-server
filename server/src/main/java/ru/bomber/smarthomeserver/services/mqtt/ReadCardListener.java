package ru.bomber.smarthomeserver.services.mqtt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;
import ru.bomber.smarthomeserver.services.TelegramService;
import ru.bomber.smarthomeserver.services.UserService;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReadCardListener implements IMqttMessageListener {

    private final TelegramService telegramService;
    private final UserService userService;

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        StringBuilder sb = new StringBuilder("Card was read: #");
        sb.append(new String(mqttMessage.getPayload(), StandardCharsets.UTF_8))
                .append(" Duplicate: ")
                .append(mqttMessage.isDuplicate())
                .append(" Retained: ")
                .append(mqttMessage.isRetained());
        log.info(sb.toString());
    }
}

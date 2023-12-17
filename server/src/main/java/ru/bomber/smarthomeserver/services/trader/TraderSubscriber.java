package ru.bomber.smarthomeserver.services.trader;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;
import ru.bomber.core.mqtt.MqttService;
import ru.bomber.core.mqtt.Topics;
import ru.bomber.core.exceptions.NotAvailableMqttService;
import ru.bomber.smarthomeserver.model.telegram.Chat;
import ru.bomber.smarthomeserver.model.telegram.TelegramChatType;
import ru.bomber.smarthomeserver.repository.telegram.ChatRepository;
import ru.bomber.smarthomeserver.services.TelegramService;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TraderSubscriber implements IMqttMessageListener {

    private final TelegramService telegramService;
    private final MqttService mqttService;

    private final ChatRepository chatRepository;

    private Long chatID;

    @PostConstruct
    private void init() {
        try {
            mqttService.subscribe(Topics.TRADER_LOG, this);
            updateChatId();
            log.info("Trader subscriber successfully started.");
        } catch (MqttException|NotAvailableMqttService e) {
            log.info("Trader subscriber can not started.");
            //throw new RuntimeException("MQTT service didn't subscribe to trader logs.", e);
        }
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        log.info("Trader subscriber received new message in topic Trader LOGs.");
        log.info("MQTT message: " + mqttMessage.toString());
        if (chatID != null) {
            telegramService.sendTextMessage(mqttMessage.toString(), chatID);
        }
    }

    public void updateChatId() {
        Optional<Chat> firstByType = chatRepository.findFirstByType(TelegramChatType.TRADER_NOTIFICATION);
        firstByType.ifPresent(chat -> chatID = chat.getId());
    }
}

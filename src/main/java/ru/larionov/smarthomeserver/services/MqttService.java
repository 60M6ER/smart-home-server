package ru.larionov.smarthomeserver.services;

import jakarta.annotation.PostConstruct;
import javassist.bytecode.ByteArray;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.larionov.smarthomeserver.services.mqtt.ReadCardListener;
import ru.larionov.smarthomeserver.services.mqtt.Topics;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class MqttService {

    @Value("${mqtt.url}")
    private String url_server;
    @Value("${mqtt.port}")
    private Integer port;
    @Value("${mqtt.user}")
    private String user;
    @Value("${mqtt.password}")
    private String password;
    @Value("${mqtt.id}")
    private String id;

    private final ReadCardListener readCardListener;

    private IMqttClient mqttClient;

    @PostConstruct
    private void init(){
        try {
            mqttClient = new MqttClient("tcp://" + url_server + ":" + port, id);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            options.setUserName(user);
            options.setPassword(password.toCharArray());
            mqttClient.connect(options);

            mqttClient.subscribe(Topics.ON_LOCK_TOPIC, (topic, msg) -> {
                log.info(new String(msg.getPayload(), StandardCharsets.UTF_8));
            });

            mqttClient.subscribe(Topics.READ_NEW_TOPIC, readCardListener);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }
}

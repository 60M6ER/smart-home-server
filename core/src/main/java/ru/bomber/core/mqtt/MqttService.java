package ru.bomber.core.mqtt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.bomber.core.exceptions.NotAvailableMqttService;
import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

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

    private IMqttClient mqttClient;

    @Getter
    private boolean active;

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
            active = true;

//            mqttClient.subscribe(Topics.ON_LOCK_TOPIC, (topic, msg) -> {
//                log.info(new String(msg.getPayload(), StandardCharsets.UTF_8));
//            });
//
//            mqttClient.subscribe(Topics.READ_NEW_TOPIC, readCardListener);
        } catch (MqttException e) {
            active = false;
            //throw new NotAvailableMqttService(e);
            log.warn("MQTT service is not available.");
        }
    }

    public void subscribe(String topic, IMqttMessageListener listener) throws MqttException,NotAvailableMqttService {
        if (!isActive()){
            //throw new NotAvailableMqttService("Not available in use.");
            log.warn("MQTT service is not available.");
        } else {
            mqttClient.subscribe(topic, listener);
        }
    }

    public void sendMessage(String topic, String message, int qos) throws MqttException,NotAvailableMqttService {
        if (!isActive()){
            //throw new NotAvailableMqttService("Not available in use.");
            log.warn("MQTT service is not available.");
        } else {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes(StandardCharsets.UTF_8));
            mqttMessage.setQos(qos);
            mqttClient.publish(topic, mqttMessage);
        }
    }
}

package ru.bomber.smarthomeserver.services.trader;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.bomber.core.mqtt.trader.models.BotDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TraderService {

    private final WebClient webClient;

    @Value("${trader.host}")
    private String host;

    public List<BotDTO> getBotList(String namePart) {
        return webClient.get()
                .uri(String.join("", host, "/v1/bots"))
                .attribute("name_part", namePart)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<BotDTO>>() {
                })
                .block();
    }

    public void saveBot(BotDTO botDTO) {
        webClient.post()
                .uri(String.join("", host, "/v1/bots"))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}

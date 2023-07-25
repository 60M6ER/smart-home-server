package ru.bomber.smarthomeserver.services.trader;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.bomber.core.trader.models.BotDTO;
import ru.bomber.core.trader.models.ExchangeVendor;
import ru.bomber.core.trader.models.Instrument;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TraderService {

    private final WebClient webClient;

    @Value("${trader.host}")
    private String host;

    public List<BotDTO> getBotList(String namePart) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("name_part", namePart);
        return webClient.get()
                .uri(String.join("", host, "/v1/bots"), parameters)
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

    public List<Instrument> getInstruments(ExchangeVendor vendor, String namePart) {
        return webClient.get()
                .uri(String.join("", host, "/v1/exchange/getInstruments?vendor={vendor}&name_part={part}"), vendor, namePart)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Instrument>>() {
                })
                .block();
    }
}

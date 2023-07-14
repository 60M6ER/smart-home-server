package ru.larionov.smarthomeserver.services.trader;

import com.cf.client.poloniex.PoloniexExchangeService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TraderService {

    @Value("${trader.api_key}")
    private String APIKey;

    @Value("${trader.secret_key}")
    private String SecretKey;

    private PoloniexExchangeService poloniexExchangeService;

    @PostConstruct
    private void init() {
        poloniexExchangeService = new PoloniexExchangeService(APIKey, SecretKey);
    }
}

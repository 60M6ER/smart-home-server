package ru.larionov.smarthomeserver.services.trader;

import com.cf.client.poloniex.PoloniexExchangeService;
import com.cf.data.model.poloniex.PoloniexFeeInfo;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.larionov.smarthomeserver.utils.exchanges.PoloClient;

@Service
@Slf4j
public class TraderService {

    @Value("${trader.api_key}")
    private String APIKey;

    @Value("${trader.secret_key}")
    private String secretKey;

    private PoloClient poloClient;

    @PostConstruct
    private void init() {
        poloClient = new PoloClient(APIKey, secretKey);
    }
}

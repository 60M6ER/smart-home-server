package ru.larionov.backend.converter;

import io.jsonwebtoken.lang.Strings;
import ru.larionov.backend.dto.exchange.ExchangeDTO;
import ru.larionov.backend.dto.exchange.ExchangeViewDTO;
import ru.larionov.backend.model.Exchange;

public class ExchangeConverter {

    public static ExchangeViewDTO toViewDTO(Exchange exchange) {
        ExchangeViewDTO exchangeViewDTO = new ExchangeViewDTO();
        exchangeViewDTO.setId(exchange.getId());
        exchangeViewDTO.setName(exchangeViewDTO.getName());
        return exchangeViewDTO;
    }

    public static ExchangeDTO toExchangeDTO(Exchange exchange) {
        ExchangeDTO exchangeDTO = new ExchangeDTO();
        exchangeDTO.setId(exchange.getId());
        exchangeDTO.setName(exchange.getName());
        exchangeDTO.setVendor(exchange.getVendor());
        exchangeDTO.setApiKey(exchange.getAPI_KEY());
        if (exchange.getSECRET().isBlank()){
            exchangeDTO.setSecret("");
        } else {
            exchangeDTO.setSecret("yes");
        }
        exchangeDTO.setActive(exchange.getActive());
        return exchangeDTO;
    }

    public static Exchange fromDTO(ExchangeDTO exchangeDTO) {
        Exchange exchange = new Exchange();
        exchange.setId(exchangeDTO.getId());
        exchange.setName(exchangeDTO.getName());
        exchange.setVendor(exchangeDTO.getVendor());
        exchange.setAPI_KEY(exchangeDTO.getApiKey());
        exchange.setSECRET(exchangeDTO.getSecret());
        exchange.setActive(exchangeDTO.getActive());
        return exchange;
    }
}

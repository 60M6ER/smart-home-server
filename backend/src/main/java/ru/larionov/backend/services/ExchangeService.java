package ru.larionov.backend.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.larionov.backend.converter.ExchangeConverter;
import ru.larionov.backend.dto.exchange.ExchangeDTO;
import ru.larionov.backend.dto.exchange.ExchangeViewDTO;
import ru.larionov.backend.exception.ExchangeNotFoundByVendor;
import ru.larionov.backend.model.Exchange;
import ru.larionov.backend.model.ExchangeVendor;
import ru.larionov.backend.repositories.ExchangeRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExchangeService {

    private final ExchangeRepository exchangeRepository;

    public List<ExchangeViewDTO> getAllExchanges() {

        return Arrays.stream(ExchangeVendor.values())
                .map(exchangeVendor -> {
                    Optional<Exchange> exchange = exchangeRepository.getExchangeByVendor(exchangeVendor);
                    ExchangeViewDTO viewDTO = new ExchangeViewDTO();
                    if (exchange.isPresent()) {
                        viewDTO.setId(exchange.get().getId());
                        viewDTO.setName(exchange.get().getName());
                        viewDTO.setVendor(exchange.get().getVendor());
                    } else {
                        viewDTO.setId(-1L);
                        viewDTO.setName(ExchangeVendor.getView(exchangeVendor));
                        viewDTO.setVendor(exchangeVendor);
                    }
                    return viewDTO;
                })
                .toList();
    }

    public ExchangeDTO getExchangeData (ExchangeVendor vendor) {
        Optional<Exchange> byVendor = exchangeRepository.getExchangeByVendor(vendor);
        if (byVendor.isPresent()) {
            return ExchangeConverter.toExchangeDTO(byVendor.get());
        } else {
            ExchangeDTO exchangeDTO = new ExchangeDTO();
            exchangeDTO.setId(-1L);
            exchangeDTO.setName(ExchangeVendor.getView(vendor));
            exchangeDTO.setVendor(vendor);
            exchangeDTO.setActive(false);
            exchangeDTO.setApiKey("");
            exchangeDTO.setSecret("");
            return exchangeDTO;
        }
    }

    @Transactional
    public void saveExchangeData (ExchangeDTO exchangeDTO) {
        Optional<Exchange> exchangeByVendor = exchangeRepository.findById(exchangeDTO.getId());
        if (exchangeByVendor.isPresent()) {
            Exchange exchange = exchangeByVendor.get();
            exchange.setAPI_KEY(exchangeDTO.getApiKey());
            if (!(exchangeDTO.getSecret().equals("yes") || exchangeDTO.getSecret().isBlank())) {
                exchange.setSECRET(exchangeDTO.getSecret());
            }
            exchange.setActive(exchangeDTO.getActive());
        }else if (exchangeDTO.getVendor() != null && exchangeDTO.getId().equals(-1L)) {
            Exchange exchange = new Exchange();
            exchange.setName(ExchangeVendor.getView(exchangeDTO.getVendor()));
            exchange.setVendor(exchangeDTO.getVendor());
            exchange.setAPI_KEY(exchangeDTO.getApiKey());
            if (!(exchangeDTO.getSecret().equals("yes") || exchangeDTO.getSecret().isBlank())) {
                exchange.setSECRET(exchangeDTO.getSecret());
            }
            exchange.setActive(exchangeDTO.getActive());
            exchangeRepository.save(exchange);
        }
        else {
            throw new ExchangeNotFoundByVendor("Exchange not found by vendor: " +exchangeDTO.getVendor().name());
        }
    }
}

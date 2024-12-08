package ru.larionov.backend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.larionov.backend.dto.exchange.ExchangeDTO;
import ru.larionov.backend.dto.exchange.ExchangeViewDTO;
import ru.larionov.backend.model.ExchangeVendor;
import ru.larionov.backend.services.ExchangeService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exchanges")
@RequiredArgsConstructor
public class ExchangeController {

    private final ExchangeService exchangeService;

    @GetMapping
    public List<ExchangeViewDTO> getExchangeList() {
        return exchangeService.getAllExchanges();
    }

    @GetMapping("/{vendor}")
    public ExchangeDTO getExchange (@PathVariable(name = "vendor") ExchangeVendor vendor) {
        return exchangeService.getExchangeData(vendor);
    }

    @PostMapping
    public String saveExchangeData(@RequestBody ExchangeDTO exchangeDTO) {
        exchangeService.saveExchangeData(exchangeDTO);
        return "ok";
    }
}

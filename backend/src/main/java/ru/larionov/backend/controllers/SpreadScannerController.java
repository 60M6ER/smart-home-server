package ru.larionov.backend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.larionov.backend.dto.portfolio.ExchangeBalance;
import ru.larionov.backend.dto.portfolio.ViewPortfolio;
import ru.larionov.backend.dto.spreads.SpreadDTO;
import ru.larionov.backend.services.PortfolioService;
import ru.larionov.backend.services.SpreadScanner;

import java.util.List;

@RestController
@RequestMapping("/api/v1/spreads")
@RequiredArgsConstructor
public class SpreadScannerController {

    private final SpreadScanner spreadScanner;


    @GetMapping()
    public List<SpreadDTO> getSpreads() {
        return spreadScanner.getSpreadsStack();
    }


}

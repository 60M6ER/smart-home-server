package ru.larionov.backend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.larionov.backend.dto.portfolio.ExchangeBalance;
import ru.larionov.backend.dto.portfolio.ViewPortfolio;
import ru.larionov.backend.exception.NetworksAreNotEquals;
import ru.larionov.backend.services.PortfolioService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;


    @GetMapping("/view")
    public ViewPortfolio getViewPortfolio() {
        return portfolioService.getViewPortfolio();
    }

    @GetMapping("/updateBalances")
    public String updateBalances() {
        portfolioService.updateBalances();
        return "OK";
    }

    @GetMapping("/exchangeBalances")
    public List<ExchangeBalance> getExchangeBalances() {
        return portfolioService.getExchangeBalances();
    }

    @GetMapping("/feeEqualizeBalances")
    public Double getFeeEqualizeBalances() throws NetworksAreNotEquals {
        return portfolioService.getFeeEqualizeBalances();
    }
}

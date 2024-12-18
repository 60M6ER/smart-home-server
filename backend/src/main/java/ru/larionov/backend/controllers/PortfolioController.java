package ru.larionov.backend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.larionov.backend.dto.portfolio.ViewPortfolio;
import ru.larionov.backend.services.PortfolioService;

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
}

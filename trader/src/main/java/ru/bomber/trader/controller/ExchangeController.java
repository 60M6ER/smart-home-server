package ru.bomber.trader.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.bomber.core.trader.models.ExchangeVendor;
import ru.bomber.core.trader.models.Instrument;
import ru.bomber.trader.services.TraderService;

import java.util.List;

@RestController
@RequestMapping("v1/exchange")
@RequiredArgsConstructor
public class ExchangeController {

    private final TraderService traderService;

    @GetMapping("/getInstruments")
    public List<Instrument> getInstruments(@RequestParam(name = "vendor") ExchangeVendor vendor,
                                           @RequestParam(name = "name_part", required = false, defaultValue = "") String namePart) {
        if (namePart.equals(""))
            return traderService.getAPI(vendor).getInstruments();
        else
            return traderService.getAPI(vendor).getInstruments(namePart);

    }
}

package ru.bomber.trader.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.bomber.core.mqtt.trader.models.BotDTO;
import ru.bomber.trader.services.BotService;

import java.util.List;

@RestController
@RequestMapping("v1/bots")
@RequiredArgsConstructor
public class BotController {

    private final BotService botService;

    @GetMapping()
    private List<BotDTO> getBotList(@RequestParam(name = "name_part", required = false, defaultValue = "") String namePart) {
        return botService.getListBots(namePart);
    }
}

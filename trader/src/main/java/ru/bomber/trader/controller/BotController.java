package ru.bomber.trader.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import retrofit2.http.Body;
import ru.bomber.core.http.Response;
import ru.bomber.core.trader.models.BotDTO;
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

    @PostMapping
    private Response saveBot(@RequestBody BotDTO botDTO) {
        return botService.saveBot(botDTO);
    }
}

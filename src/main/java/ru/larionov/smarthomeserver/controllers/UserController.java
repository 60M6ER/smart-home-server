package ru.larionov.smarthomeserver.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.larionov.smarthomeserver.dto.user.UserCodeTelegram;
import ru.larionov.smarthomeserver.model.User;
import ru.larionov.smarthomeserver.services.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/registration_telegram")
    public UserCodeTelegram registrationTelegramUser(@RequestBody UserCodeTelegram userCodeTelegram) {
        return userService.getTelegramRegistrationCode(userCodeTelegram);
    }

    @GetMapping("")
    public User getUser(@RequestParam("id") Long id) {
        return userService.getUserById(id);
    }
}

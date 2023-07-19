package ru.bomber.smarthomeserver.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.bomber.smarthomeserver.dto.user.UserCodeTelegram;
import ru.bomber.smarthomeserver.model.User;
import ru.bomber.smarthomeserver.services.UserService;

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

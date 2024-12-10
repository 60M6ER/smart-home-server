package ru.larionov.backend.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.larionov.backend.dto.user.UserCodeTelegram;
import ru.larionov.backend.dto.user.UserDTO;
import ru.larionov.backend.services.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/registration_telegram")
    public UserCodeTelegram registrationTelegramUser(@RequestBody UserCodeTelegram userCodeTelegram) {
        return userService.getTelegramRegistrationCode(userCodeTelegram);
    }

    @GetMapping("/{username}")
    public UserDTO getUser(@PathVariable("username") String username) {
        return userService.getUserDtoByUsername(username);
    }
}

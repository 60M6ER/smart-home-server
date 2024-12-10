package ru.larionov.backend.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.larionov.backend.model.Role;
import ru.larionov.backend.model.User;
import ru.larionov.backend.repositories.UserRepository;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitService {
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TelegramService telegramService;

    @Value("${user.username}")
    private String username;
    @Value("${user.password}")
    private String password;
    @Value("${user.name}")
    private String name;

    @PostConstruct
    private void initUsers() {
        if (userRepository.count() == 0) {
            User user = new User();
            user.setName(name);
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setNeedChangePassword(true);
            user.addRole(Role.ROLE_ADMINISTRATOR);
            user = userRepository.save(user);
            log.info("Created user with id {}", user.getId());
        }
        telegramService.sendNotification("Сервер запущен.");
    }
}

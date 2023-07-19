package ru.bomber.smarthomeserver.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.bomber.smarthomeserver.model.Role;
import ru.bomber.smarthomeserver.model.User;
import ru.bomber.smarthomeserver.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

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
    }
}

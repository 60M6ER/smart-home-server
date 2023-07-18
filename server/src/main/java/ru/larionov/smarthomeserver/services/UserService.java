package ru.larionov.smarthomeserver.services;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.larionov.smarthomeserver.dto.user.UserCodeTelegram;
import ru.larionov.smarthomeserver.exeptions.UserNotFound;
import ru.larionov.smarthomeserver.model.User;
import ru.larionov.smarthomeserver.repository.UserRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class UserService implements UserDetailsService {

    private static final Long TIME_TO_LIVE_CODE = 1000 * 60 * 5L;
    private final Object lock = new Object();

    private final UserRepository userRepository;

    private HashMap<User, UserCodeTelegram> registrationCodes = new HashMap<>();

    public UserCodeTelegram getTelegramRegistrationCode(UserCodeTelegram userCodeTelegram) throws UserNotFound {
        Optional<User> user = userRepository.findById(userCodeTelegram.getUserId());
        if (user.isEmpty()) {
            throw new UserNotFound(userCodeTelegram.getUserId().toString());
        }else {
            String code = String.valueOf((int) (100_000 + Math.random() * 899_999));
            UserCodeTelegram newUserCodeTelegram = new UserCodeTelegram(userCodeTelegram.getUserId(), code);
            registrationCodes.put(user.get(), newUserCodeTelegram);
            return newUserCodeTelegram;
        }
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("id"));
    }

    @Scheduled(fixedRate = 3000)
    private void killCodes() {
        synchronized (lock) {
            if (registrationCodes.size() > 0) {
                List<User> deleteUsers = new ArrayList<>();
                registrationCodes.forEach((user, userCodeTelegram) -> {
                    if (System.currentTimeMillis() - userCodeTelegram.getDateCreate() > TIME_TO_LIVE_CODE)
                        deleteUsers.add(user);
                });
                deleteUsers.forEach(registrationCodes::remove);
            }
        }
    }

    public Long getUserIdByCodeTelegram(String code) throws UserNotFound {
        User user = null;
        synchronized (lock) {
            for (Map.Entry<User, UserCodeTelegram> entry:
                    registrationCodes.entrySet()) {
                if (entry.getValue().getCode().equals(code)) {
                    user = entry.getKey();
                }
            }
            if (user != null) {
                registrationCodes.remove(user);
                return user.getId();
            }else {
                throw new UserNotFound("telegram code " + code);
            }
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles()
        );
    }
}

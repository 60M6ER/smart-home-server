package ru.bomber.smarthomeserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bomber.smarthomeserver.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> getUserByTelegramIdAndChatId(Long telegramId, Long chatId);
    Optional<User> getUserByChatId(Long chatId);
    Optional<User> findByUsername(String username);

}

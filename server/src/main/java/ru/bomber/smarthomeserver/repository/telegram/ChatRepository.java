package ru.bomber.smarthomeserver.repository.telegram;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bomber.smarthomeserver.model.telegram.Chat;
import ru.bomber.smarthomeserver.model.telegram.TelegramChatType;

import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    Optional<Chat> findFirstByType(TelegramChatType type);
}

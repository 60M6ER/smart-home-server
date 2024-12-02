package ru.bomber.smarthomeserver.model.telegram;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "chats")
public class Chat {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "type_chat")
    @Enumerated(EnumType.ORDINAL)
    private TelegramChatType type;
}

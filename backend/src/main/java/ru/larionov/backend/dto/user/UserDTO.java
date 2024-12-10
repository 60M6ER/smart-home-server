package ru.larionov.backend.dto.user;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String name;
    private boolean isTelegram;
}

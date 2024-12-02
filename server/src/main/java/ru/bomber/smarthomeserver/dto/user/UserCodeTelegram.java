package ru.bomber.smarthomeserver.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserCodeTelegram {
    private Long userId;
    private String code;

    private Long dateCreate;

    public UserCodeTelegram(Long userId, String code) {
        this.userId = userId;
        this.code = code;
        dateCreate = System.currentTimeMillis();
    }
}

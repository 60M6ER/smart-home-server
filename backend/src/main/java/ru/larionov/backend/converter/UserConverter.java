package ru.larionov.backend.converter;

import ru.larionov.backend.dto.user.UserDTO;
import ru.larionov.backend.model.User;

public class UserConverter {
    public static UserDTO toDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setUsername(user.getUsername());
        userDTO.setTelegram(user.getChatId() != null && user.getChatId() != 0);
        return userDTO;
    }
}

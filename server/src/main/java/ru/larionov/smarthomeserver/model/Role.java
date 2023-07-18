package ru.larionov.smarthomeserver.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    ROLE_ADMINISTRATOR, ROLE_GUEST;

    @Override
    public String getAuthority() {
        return name();
    }
}

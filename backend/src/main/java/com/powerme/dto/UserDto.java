package com.powerme.dto;

import java.util.Set;

/**
 * DTO de l'User renvoyé au front avec les infos minimales (sans le hash du password).
 */
public class UserDto {

    private final Long id;
    private final String email;
    private final Set<String> roles;

    public UserDto(Long id, String email, Set<String> roles) {
        this.id = id;
        this.email = email;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Set<String> getRoles() {
        return roles;
    }
}

package com.powerme.dto;

import java.util.Set;

/**
 * DTO de l'User renvoyé au front avec les infos minimales (sans le hash du password).
 */
public record UserDto(

    Long id,
    String email,
    Set<String> roles
){}

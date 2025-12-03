package com.powerme.dto;

/**
 * DTO qui contient ce qui est renvoyé après un login réussi.
 */
public record LoginResponseDto(

    String accessToken,
    UserDto user
){}

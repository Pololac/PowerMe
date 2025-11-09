package com.powerme.dto;

/**
 * DTO qui contient ce qui est renvoyé après un login réussi.
 */
public class LoginResponseDto {

    private final String accessToken;
    private final UserDto user;

    public LoginResponseDto(String accessToken, UserDto user) {
        this.accessToken = accessToken;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public UserDto getUser() {
        return user;
    }
}

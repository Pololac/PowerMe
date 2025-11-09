package com.powerme.dto;

/**
 * DTO renvoyé au front après un refresh du JWT : nouvel access token dans le body (refresh remis en
 * cookie).
 */
public class RefreshResponseDto {

    private final String accessToken;

    public RefreshResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}

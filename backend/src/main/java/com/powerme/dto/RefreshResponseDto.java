package com.powerme.dto;

/**
 * DTO renvoyé au front après un refresh du JWT : nouvel access token dans le body (refresh remis en
 * cookie).
 */
public record RefreshResponseDto(String accessToken) {}

package com.powerme.dto;

/**
 * DTO de ChargingLocation renvoyé au front avec les infos minimales.
 */
public record ChargingLocationMapDto(
        Long id,
        String name,
        double latitude,
        double longitude
) {

}

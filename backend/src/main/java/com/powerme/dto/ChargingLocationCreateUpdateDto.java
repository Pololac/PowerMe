package com.powerme.dto;

public record ChargingLocationCreateUpdateDto(
        String name,
        String street,
        String postalCode,
        String city,
        String country,
        double latitude,
        double longitude
) {

}

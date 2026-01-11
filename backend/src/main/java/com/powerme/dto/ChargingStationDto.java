package com.powerme.dto;

import com.powerme.enums.SocketType;
import com.powerme.enums.StationStatus;

/**
 * DTO de ChargingLocation renvoyé au front avec les infos minimales.
 */
public record ChargingStationDto (
        Long id,
        String name,
        SocketType socketType,
        double powerKw,
        StationStatus status,
        String imagePath
) {

    }


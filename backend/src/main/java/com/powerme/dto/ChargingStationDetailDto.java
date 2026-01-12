package com.powerme.dto;

import com.powerme.enums.SocketType;
import com.powerme.enums.StationStatus;
import java.time.LocalTime;

public record ChargingStationDetailDto(
        Long id,
        String name,
        SocketType socketType,
        double powerKw,
        StationStatus status,
        String imagePath,
        double hourlyRate,
        boolean active,
        LocalTime availableFrom,
        LocalTime availableTo
) {
}

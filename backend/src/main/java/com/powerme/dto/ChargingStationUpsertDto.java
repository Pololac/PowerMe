package com.powerme.dto;

import com.powerme.enums.ChargingPower;
import com.powerme.enums.SocketType;
import java.math.BigDecimal;
import java.time.LocalTime;

public record ChargingStationUpsertDto(
        String name,
        SocketType socketType,
        ChargingPower power,
        BigDecimal hourlyRate,
        boolean active,
        LocalTime availableFrom,
        LocalTime availableTo
) {}

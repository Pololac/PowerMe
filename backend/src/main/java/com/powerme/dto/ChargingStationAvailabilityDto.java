package com.powerme.dto;

import java.time.LocalDate;
import java.util.List;

public record ChargingStationAvailabilityDto(
        LocalDate date,
        List<TimeSlotDto> slots) {
}

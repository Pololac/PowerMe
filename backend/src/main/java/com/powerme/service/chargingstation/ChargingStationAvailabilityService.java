package com.powerme.service.chargingstation;

import com.powerme.dto.ChargingStationAvailabilityDto;
import java.time.LocalDate;

public interface ChargingStationAvailabilityService {
    ChargingStationAvailabilityDto getAvailability(
            Long stationId,
            LocalDate date
    );
}

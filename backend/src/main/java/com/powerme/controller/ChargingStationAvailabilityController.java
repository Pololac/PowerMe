package com.powerme.controller;

import com.powerme.dto.ChargingStationAvailabilityDto;
import com.powerme.service.chargingstation.ChargingStationAvailabilityService;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/charging-stations")
public class ChargingStationAvailabilityController {
    private final ChargingStationAvailabilityService availabilityService;

    public ChargingStationAvailabilityController(
            ChargingStationAvailabilityService availabilityService
    ) {
        this.availabilityService = availabilityService;
    }

    @GetMapping("/{id}/availability")
    public ChargingStationAvailabilityDto getAvailability(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return availabilityService.getAvailability(id, date);
    }
}

package com.powerme.controller;

import com.powerme.dto.ChargingStationDetailDto;
import com.powerme.dto.ChargingStationUpsertDto;
import com.powerme.entity.ChargingStation;
import com.powerme.enums.StationStatus;
import com.powerme.mapper.ChargingStationMapper;
import com.powerme.service.chargingstation.ChargingStationService;
import jakarta.validation.Valid;
import java.time.Instant;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/charging-stations")
public class ChargingStationController {

    private final ChargingStationService stationService;
    private final ChargingStationMapper stationMapper;

    public ChargingStationController(
            ChargingStationService stationService,
            ChargingStationMapper stationMapper
    ) {
        this.stationService = stationService;
        this.stationMapper = stationMapper;
    }

    @GetMapping("/{id}")
    public ChargingStationDetailDto getStation(@PathVariable Long id) {
        ChargingStation station = stationService.getById(id);

        StationStatus status =
                stationService.computeStatus(station, Instant.now());

        return stationMapper.toDetailDto(station, status);
    }

    @PostMapping("/{locationId}/stations")
    public ChargingStationDetailDto create(
            @PathVariable Long locationId,
            @RequestBody @Valid ChargingStationUpsertDto dto
    ) {
        ChargingStation station =
                stationService.create(locationId, dto);

        StationStatus status =
                stationService.computeStatus(station, Instant.now());

        return stationMapper.toDetailDto(station, status);
    }

    @PutMapping("/{id}")
    public ChargingStationDetailDto update(
            @PathVariable Long id,
            @RequestBody @Valid ChargingStationUpsertDto dto
    ) {
        ChargingStation station =
                stationService.update(id, dto);

        StationStatus status =
                stationService.computeStatus(station, Instant.now());

        return stationMapper.toDetailDto(station, status);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        stationService.delete(id);
    }
}

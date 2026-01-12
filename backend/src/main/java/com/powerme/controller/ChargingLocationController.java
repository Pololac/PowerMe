package com.powerme.controller;

import com.powerme.dto.ChargingLocationCreateUpdateDto;
import com.powerme.dto.ChargingLocationDetailDto;
import com.powerme.dto.ChargingLocationMapDto;
import com.powerme.dto.ChargingStationDto;
import com.powerme.entity.ChargingLocation;
import com.powerme.enums.StationStatus;
import com.powerme.mapper.ChargingLocationMapper;
import com.powerme.mapper.ChargingStationMapper;
import com.powerme.service.charginglocation.ChargingLocationService;
import com.powerme.service.chargingstation.ChargingStationService;
import com.powerme.service.security.UserPrincipal;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/charging-locations")
public class ChargingLocationController {

    private final ChargingLocationService locationService;
    private final ChargingLocationMapper locationMapper;
    private final ChargingStationService stationService;
    private final ChargingStationMapper stationMapper;

    public ChargingLocationController(
            ChargingLocationService locationService,
            ChargingLocationMapper locationMapper,
            ChargingStationService stationService,
            ChargingStationMapper stationMapper
    ) {
        this.locationService = locationService;
        this.locationMapper = locationMapper;
        this.stationService = stationService;
        this.stationMapper = stationMapper;
    }

    @GetMapping("/bounds")
    public List<ChargingLocationMapDto> getInBounds(
            @RequestParam double north,
            @RequestParam double south,
            @RequestParam double east,
            @RequestParam double west
    ) {
        return locationService
                .getLocationsInBounds(north, south, east, west)
                .stream()
                .map(locationMapper::toMapDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ChargingLocationDetailDto getById(@PathVariable Long id) {
        // Charge location avec stations
        ChargingLocation location = locationService.getByIdWithStations(id);

        // Décide du "maintenant" et mappe les stations une par une pour ajouter le "now"
        Instant now = Instant.now();
        List<ChargingStationDto> stationDtos = location.getChargingStations().stream()
                .map(station -> {
                    StationStatus status =
                            stationService.computeStatus(station, now);
                    return stationMapper.toDto(station, status);
                })
                .toList();

        // Mappe la location sans les stations
        ChargingLocationDetailDto baseDto = locationMapper.toDetailDto(location, stationDtos.size());

        // Renvoie le DTO complet
        return ChargingLocationDetailDto.of(baseDto, stationDtos);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChargingLocationDetailDto create(
            @RequestBody @Valid ChargingLocationCreateUpdateDto dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ChargingLocation location =
                locationService.create(dto, principal);

        return locationMapper.toDetailDto(location);
    }

    @PutMapping("/{id}")
    public ChargingLocationDetailDto update(
            @PathVariable Long id,
            @RequestBody @Valid ChargingLocationCreateUpdateDto dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ChargingLocation location =
                locationService.update(id, dto, principal);

        return locationMapper.toDetailDto(location);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        locationService.delete(id, principal);
    }

}

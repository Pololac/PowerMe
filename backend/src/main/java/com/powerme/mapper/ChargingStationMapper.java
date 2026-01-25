package com.powerme.mapper;

import com.powerme.dto.ChargingStationDetailDto;
import com.powerme.dto.ChargingStationDto;
import com.powerme.dto.ChargingStationUpsertDto;
import com.powerme.entity.ChargingStation;
import com.powerme.enums.StationStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ChargingStationMapper {

    @Mapping(target = "powerKw", source = "station.power.kilowatts")
    ChargingStationDto toDto(
            ChargingStation station,
            StationStatus status
    );

    @Mapping(target = "powerKw", source = "station.power.kilowatts")
    ChargingStationDetailDto toDetailDto(
            ChargingStation station,
            StationStatus status
    );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "chargingLocation", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "unavailabilityPeriods", ignore = true)
    ChargingStation fromUpsertDto(ChargingStationUpsertDto dto);
}

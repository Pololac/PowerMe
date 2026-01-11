package com.powerme.mapper;

import com.powerme.dto.ChargingStationDto;
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
}

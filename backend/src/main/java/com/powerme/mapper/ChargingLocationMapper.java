package com.powerme.mapper;

import com.powerme.dto.ChargingLocationDetailDto;
import com.powerme.dto.ChargingLocationMapDto;
import com.powerme.entity.Address;
import com.powerme.entity.ChargingLocation;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ChargingLocationMapper {

    @Mapping(target = "latitude", expression = "java(toDouble(location.getLatitude()))")
    @Mapping(target = "longitude", expression = "java(toDouble(location.getLongitude()))")
    ChargingLocationMapDto toMapDto(ChargingLocation location);

    default double toDouble(BigDecimal value) {
        return value != null ? value.doubleValue() : 0.0;
    }


    @Mapping(
            target = "address",
            expression = "java(toFullAddress(location.getAddress()))"
    )
    ChargingLocationDetailDto toDetailDto(ChargingLocation location, int stationsCount);

    /**
     * Variante utilisée après create / update.
     * Une location nouvellement créée n’a aucune station.
     */
    @Mapping(
            target = "address",
            expression = "java(toFullAddress(location.getAddress()))"
    )
    ChargingLocationDetailDto toDetailDto(
            ChargingLocation location
    );

    /**
     * Mappe l'adresse complète.
     * ⚠️ L'Address DOIT être déjà chargée (JOIN FETCH côté repository).
     */
    default String toFullAddress(Address address) {
        return address != null ? address.getFullAddress() : null;
    }
}

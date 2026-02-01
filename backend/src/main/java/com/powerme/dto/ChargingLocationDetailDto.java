package com.powerme.dto;

import java.util.List;

public record ChargingLocationDetailDto(
        Long id,
        String name,
        String address,
        double latitude,
        double longitude,
        String imagePath,
        List<ChargingStationDto> stations,
        int stationsCount
) {
    // Factory
    public static ChargingLocationDetailDto of(
            ChargingLocationDetailDto base,
            List<ChargingStationDto> stations
    ) {
        return new ChargingLocationDetailDto(
                base.id(),
                base.name(),
                base.address(),
                base.latitude(),
                base.longitude(),
                base.imagePath,
                stations,
                stations.size()
        );
    }
}

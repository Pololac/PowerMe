package com.powerme.service.charginglocation;

import com.powerme.dto.ChargingLocationCreateUpdateDto;
import com.powerme.entity.ChargingLocation;
import com.powerme.entity.ChargingStation;
import com.powerme.enums.StationStatus;
import com.powerme.service.security.UserPrincipal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public interface ChargingLocationService {

    List<ChargingLocation> getLocationsInBounds(
            double north,
            double south,
            double east,
            double west
    );

    ChargingLocation getById(Long id);

    ChargingLocation getByIdWithStations(Long id);

    int countStations(Long locationId);

    StationStatus computeStatus(ChargingStation station, Instant now);

    ChargingLocation create(ChargingLocationCreateUpdateDto dto, UserPrincipal principal);

    ChargingLocation update(Long id, ChargingLocationCreateUpdateDto dto, UserPrincipal principal);

    void delete(Long id, UserPrincipal principal);

}

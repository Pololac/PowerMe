package com.powerme.service.charginglocation;

import com.powerme.dto.ChargingLocationCreateUpdateDto;
import com.powerme.entity.ChargingLocation;
import com.powerme.service.security.UserPrincipal;
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

    ChargingLocation create(ChargingLocationCreateUpdateDto dto, UserPrincipal principal);

    ChargingLocation update(Long id, ChargingLocationCreateUpdateDto dto, UserPrincipal principal);

    void delete(Long id, UserPrincipal principal);

}

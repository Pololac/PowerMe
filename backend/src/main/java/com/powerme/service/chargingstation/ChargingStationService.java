package com.powerme.service.chargingstation;

import com.powerme.dto.ChargingStationUpsertDto;
import com.powerme.entity.ChargingStation;
import com.powerme.enums.StationStatus;
import java.time.Instant;

public interface ChargingStationService {

    ChargingStation getById(Long id);

    StationStatus computeStatus(ChargingStation station, Instant now);

    ChargingStation create(Long locationId, ChargingStationUpsertDto dto);

    ChargingStation update(Long id, ChargingStationUpsertDto dto);

    void delete(Long id);
}

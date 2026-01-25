package com.powerme.service.chargingstation;

import com.powerme.dto.ChargingStationUpsertDto;
import com.powerme.entity.ChargingLocation;
import com.powerme.entity.ChargingStation;
import com.powerme.enums.StationStatus;
import com.powerme.exception.ChargingStationNotFoundException;
import com.powerme.exception.ServiceException;
import com.powerme.mapper.ChargingStationMapper;
import com.powerme.repository.BookingRepository;
import com.powerme.repository.ChargingStationRepository;
import com.powerme.service.charginglocation.ChargingLocationService;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ChargingStationServiceImpl implements ChargingStationService {

    private static final Logger logger = LoggerFactory.getLogger(ChargingStationServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final ChargingStationRepository chargingStationRepository;
    private final ChargingLocationService chargingLocationService;
    private final ChargingStationMapper chargingStationMapper;

    public ChargingStationServiceImpl(BookingRepository bookingRepository, ChargingStationRepository chargingStationRepository, ChargingLocationService chargingLocationService, ChargingStationMapper chargingStationMapper) {
        this.bookingRepository = bookingRepository;
        this.chargingStationRepository = chargingStationRepository;
        this.chargingLocationService = chargingLocationService;
        this.chargingStationMapper = chargingStationMapper;
    }

    @Override
    public ChargingStation getById(Long id) {
        return chargingStationRepository.findById(id)
                .orElseThrow(() -> new ChargingStationNotFoundException(id));
    }

    @Override
    public StationStatus computeStatus(ChargingStation station, Instant now){
        boolean isBooked = bookingRepository.existsActiveBookingAt(station.getId(), now);

        if (isBooked) {
            return StationStatus.OCCUPIED;
        }
        if (!station.isActive()) {
            return StationStatus.UNAVAILABLE;
        }
        return StationStatus.AVAILABLE;
    }

    @Override
    public ChargingStation create(
            Long locationId,
            ChargingStationUpsertDto dto
    ) {
        logger.info("Creating charging station for location {}", locationId);

        ChargingLocation location =
                chargingLocationService.getById(locationId);

        ChargingStation station =
                chargingStationMapper.fromUpsertDto(dto);

        station.setChargingLocation(location);

        ChargingStation saved =
                chargingStationRepository.save(station);

        logger.info(
                "Charging station {} created for location {}",
                saved.getId(),
                locationId
        );

        return saved;
    }

    @Override
    public ChargingStation update(
            Long id,
            ChargingStationUpsertDto dto
    ) {
        logger.info("Updating charging station {}", id);

        ChargingStation station = getById(id);

        station.setName(dto.name());
        station.setSocketType(dto.socketType());
        station.setPower(dto.power());
        station.setHourlyRate(dto.hourlyRate());
        station.setActive(dto.active());
        station.setAvailableFrom(dto.availableFrom());
        station.setAvailableTo(dto.availableTo());

        ChargingStation updated =
                chargingStationRepository.save(station);

        logger.info("Charging station {} updated", id);

        return updated;
    }

    @Override
    public void delete(Long id) {
        logger.info("Deleting charging station {}", id);

        ChargingStation station = getById(id);

        if (!station.getBookings().isEmpty()) {
            logger.warn(
                    "Cannot delete charging station {}: bookings exist",
                    id
            );
            throw new ServiceException(
                    "Cannot delete a station with bookings"
            );
        }

        chargingStationRepository.delete(station);

        logger.info("Charging station {} deleted", id);
    }
}

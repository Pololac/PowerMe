package com.powerme.service.charginglocation;

import com.powerme.dto.ChargingLocationCreateUpdateDto;
import com.powerme.entity.Address;
import com.powerme.entity.ChargingLocation;
import com.powerme.entity.ChargingStation;
import com.powerme.entity.User;
import com.powerme.enums.StationStatus;
import com.powerme.exception.ChargingLocationNotFoundException;
import com.powerme.repository.AddressRepository;
import com.powerme.repository.BookingRepository;
import com.powerme.repository.ChargingLocationRepository;
import com.powerme.repository.ChargingStationRepository;
import com.powerme.repository.UserRepository;
import com.powerme.service.security.UserPrincipal;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ChargingLocationServiceImpl implements ChargingLocationService {

    private static final Logger logger = LoggerFactory.getLogger(ChargingLocationServiceImpl.class);

    private final UserRepository userRepository;
    private final ChargingLocationRepository locationRepository;
    private final ChargingStationRepository stationRepository;
    private final BookingRepository bookingRepository;
    private final AddressRepository addressRepository;

    public ChargingLocationServiceImpl(
            UserRepository userRepository,
            ChargingLocationRepository locationRepository,
            ChargingStationRepository stationRepository,
            BookingRepository bookingRepository,
            AddressRepository addressRepository
    ) {
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.stationRepository = stationRepository;
        this.bookingRepository = bookingRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public List<ChargingLocation> getLocationsInBounds(
            double north,
            double south,
            double east,
            double west
    ) {
        logger.debug("Getting locations in bounds: north={}, south={}, east={}, west={}", north,
                south, east, west);

        List<Long> ids = locationRepository.findIdsInBounds(
                north, south, east, west
        );

        if (ids.isEmpty()) {
            return List.of();
        }

        List<ChargingLocation> locations = locationRepository.findAllWithAddressByIdIn(ids);

        logger.debug("Found {} charging locations", locations.size());

        return locations;
    }

    @Override
    public ChargingLocation getById(Long id) {
        return locationRepository.findByIdWithAddress(id)
                .orElseThrow(() -> new ChargingLocationNotFoundException(id));
    }

    @Override
    public ChargingLocation getByIdWithStations(Long id) {
        return locationRepository.findByIdWithAddressAndStations(id)
                .orElseThrow(() -> new ChargingLocationNotFoundException(id));
    }

    @Override
    public int countStations(Long locationId) {
        return stationRepository.countByLocationId(locationId);
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
    @Transactional
    public ChargingLocation create(
            ChargingLocationCreateUpdateDto dto,
            UserPrincipal principal
    ) {
        logger.info(
                "Creating charging location for user {}",
                principal.getId()
        );
        User owner = loadUser(principal);

        ChargingLocation location = new ChargingLocation();
        applyDto(location, dto);
        location.setOwner(owner);

        location = locationRepository.save(location);

        logger.info(
                "Charging location {} created for user {}",
                location.getId(),
                principal.getId()
        );

        return location;
    }

    @Override
    @Transactional
    public ChargingLocation update(
            Long id,
            ChargingLocationCreateUpdateDto dto,
            UserPrincipal principal
    ) {
        logger.info(
                "Updating charging location {} by user {}",
                id,
                principal.getId()
        );

        ChargingLocation location = locationRepository.findById(id)
                .orElseThrow(() -> new ChargingLocationNotFoundException(id));

        checkOwnership(location, principal);
        applyDto(location, dto);

        return location;
    }

    @Override
    @Transactional
    public void delete(Long id, UserPrincipal principal) {
        logger.warn(
                "Deleting charging location {} by user {}",
                id,
                principal.getId()
        );

        ChargingLocation location = locationRepository.findById(id)
                .orElseThrow(() -> new ChargingLocationNotFoundException(id));

        checkOwnership(location, principal);
        locationRepository.delete(location);
    }

    /* ========== HELPERS ========== */

    private User loadUser(UserPrincipal principal) {
        return userRepository.findById(principal.getId())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user not found with id " + principal.getId()
                        )
                );
    }

    private void checkOwnership(
            ChargingLocation location,
            UserPrincipal principal
    ) {
        if (!location.getOwner().getId().equals(principal.getId())) {
            throw new AccessDeniedException("User is not owner of this charging location");
        }
    }

    private void applyDto(
            ChargingLocation location,
            ChargingLocationCreateUpdateDto dto
    ) {
        location.setName(dto.name());
        location.setLatitude(BigDecimal.valueOf(dto.latitude()));
        location.setLongitude(BigDecimal.valueOf(dto.longitude()));

        Address address = addressRepository
                .findByStreetAddressAndPostalCodeAndCityAndCountry(
                        dto.street(),
                        dto.postalCode(),
                        dto.city(),
                        dto.country()
                )
                .orElseGet(() -> {
                    Address newAddress = new Address();
                    newAddress.setStreetAddress(dto.street());
                    newAddress.setPostalCode(dto.postalCode());
                    newAddress.setCity(dto.city());
                    newAddress.setCountry(dto.country());
                    return addressRepository.save(newAddress);
                });

        location.setAddress(address);
    }
}

package com.powerme.service.booking;

import com.powerme.dto.BookingCreateRequestDto;
import com.powerme.entity.Address;
import com.powerme.entity.Booking;
import com.powerme.entity.ChargingLocation;
import com.powerme.entity.ChargingStation;
import com.powerme.entity.User;
import com.powerme.enums.BookingStatus;
import com.powerme.exception.BookingConflictException;
import com.powerme.exception.BookingNotFoundException;
import com.powerme.exception.ChargingStationNotFoundException;
import com.powerme.exception.UserNotFoundException;
import com.powerme.repository.BookingRepository;
import com.powerme.repository.ChargingStationRepository;
import com.powerme.repository.UserRepository;
import com.powerme.service.pricing.PricingService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingServiceImpl implements BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ChargingStationRepository chargingStationRepository;
    private final PricingService pricingService;
    private final SlotService slotService;

    public BookingServiceImpl(UserRepository userRepository, BookingRepository bookingRepository, ChargingStationRepository chargingStationRepository, PricingService pricingService, SlotService slotService) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.chargingStationRepository = chargingStationRepository;
        this.pricingService = pricingService;
        this.slotService = slotService;
    }

    /**
     * Liste toutes les réservations d’un utilisateur.
     */
    @Transactional(readOnly = true)
    public List<Booking> getBookingsForUser(Long userId) {
        return bookingRepository.findByUserIdOrderByStartTimeDesc(userId);
    }

    /**
     * Récupère une réservation précise appartenant à l’utilisateur.
     */
    @Transactional(readOnly = true)
    public Booking getBookingForUser(Long bookingId, Long userId) {
        return bookingRepository.findByIdAndUserId(bookingId, userId)
                .orElseThrow(BookingNotFoundException::new);
    }

    /**
     * Création d'une réservation.
     * Le backend est la source de vérité :
     * - reconstruction des créneaux
     * - détection des conflits
     * - calcul du prix final
     */
    @Override
    public Booking createBooking(
            BookingCreateRequestDto request,
            Long userId
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        ChargingStation station = chargingStationRepository
                .findByIdWithLocationAndAddress(request.stationId())
                .orElseThrow(ChargingStationNotFoundException::new);

        ChargingLocation location = station.getChargingLocation();
        Address address = location.getAddress();

        // Reconstruction des créneaux
        SlotService.SlotRange range =
                slotService.computeRange(request.date(), request.slots());

        // Détection des conflits
        boolean conflict = bookingRepository.existsOverlap(
                station.getId(),
                range.start(),
                range.end()
        );

        if (conflict) {
            throw new BookingConflictException(
                    "Un ou plusieurs créneaux sont déjà réservés",
                    request.slots()
            );
        }

        // Calcul du prix final
        BigDecimal totalPrice = pricingService.computePrice(
                station.getHourlyRate(),
                range.slots()
        );

        // Création de la réservation
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setChargingStation(station);
        booking.setStartTime(range.start());
        booking.setEndTime(range.end());
        booking.setTotalPrice(totalPrice);
        booking.setBookingStatus(BookingStatus.PENDING);

        // Snapshots (utilisés pour l’historique)
        booking.setStationNameSnapshot(station.getName());
        booking.setHourlyRateSnapshot(station.getHourlyRate());
        booking.setStationAddressSnapshot(address.getFullAddress());

        return bookingRepository.save(booking);
    }


    // ==========================
    // Helpers métier privés
    // ==========================

    private Instant computeStartTime(
            LocalDate date,
            List<Integer> slots
    ) {
        int firstSlot = Collections.min(slots);

        return date
                .atStartOfDay()
                .plusMinutes(firstSlot * 30L)
                .atZone(ZoneId.systemDefault())
                .toInstant();
    }

    private Instant computeEndTime(
            LocalDate date,
            List<Integer> slots
    ) {
        int lastSlot = Collections.max(slots) + 1;

        return date
                .atStartOfDay()
                .plusMinutes(lastSlot * 30L)
                .atZone(ZoneId.systemDefault())
                .toInstant();
    }

    private String buildFullAddress(ChargingLocation location) {
        return String.format("%s", location.getAddress().getFullAddress());
    }
}

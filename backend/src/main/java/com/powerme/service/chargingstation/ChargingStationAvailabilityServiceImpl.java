package com.powerme.service.chargingstation;

import com.powerme.dto.ChargingStationAvailabilityDto;
import com.powerme.dto.TimeSlotDto;
import com.powerme.entity.Booking;
import com.powerme.entity.ChargingStation;
import com.powerme.exception.ChargingStationNotFoundException;
import com.powerme.repository.BookingRepository;
import com.powerme.repository.ChargingStationRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ChargingStationAvailabilityServiceImpl implements ChargingStationAvailabilityService {
    private final BookingRepository bookingRepository;
    private final ChargingStationRepository stationRepository;

    public ChargingStationAvailabilityServiceImpl(
            BookingRepository bookingRepository,
            ChargingStationRepository stationRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.stationRepository = stationRepository;
    }

    @Override
    public ChargingStationAvailabilityDto getAvailability(
            Long stationId,
            LocalDate date
    ) {
        ChargingStation station = stationRepository.findById(stationId)
                .orElseThrow(() -> new ChargingStationNotFoundException(stationId));

        // Déterminer la plage horaire journalière (option possible du propriétaire)
        LocalTime stationFrom = station.getAvailableFrom();
        LocalTime stationTo = station.getAvailableTo();

        LocalTime from;
        LocalTime to;

        if (stationFrom == null || stationTo == null) {
            from = LocalTime.of(0, 0);
            to   = LocalTime.of(23, 59);
        } else {
            from = stationFrom;
            to   = stationTo;
        }

        // Calculer les bornes de la journée en Instant (car Booking en Instant)
        ZoneId zone = ZoneId.systemDefault();

        Instant startOfDay = date.atStartOfDay(zone).toInstant();
        Instant endOfDay = date.plusDays(1).atStartOfDay(zone).toInstant();

        // Récupérer les bookings actifs ce jour-là
        List<Booking> bookings =
                bookingRepository.findActiveBookingsForDay(
                        stationId,
                        startOfDay,
                        endOfDay
                );

        // Générer les slots
        List<TimeSlotDto> slots =
                generateSlots(date, from, to, bookings, zone);

        return new ChargingStationAvailabilityDto(date, slots);
    }

    private List<TimeSlotDto> generateSlots(
            LocalDate date,
            LocalTime from,
            LocalTime to,
            List<Booking> bookings,
            ZoneId zone
    ) {
        List<TimeSlotDto> slots = new ArrayList<>();

        long totalMinutes = java.time.Duration.between(from, to).toMinutes();

        for (long minutes = 0; minutes + 30 <= totalMinutes; minutes += 30) {
            LocalTime slotStart = from.plusMinutes(minutes);
            LocalTime slotEnd = slotStart.plusMinutes(30);

            int slotIndex = (int) (
                    java.time.Duration.between(
                            LocalTime.MIDNIGHT,
                            slotStart
                    ).toMinutes() / 30
            );

            // Définit pour le slot si y'a un booking : retourne "true" si dispo
            boolean available = bookings.stream().noneMatch(
                    booking -> overlapsBooking(date, slotStart, slotEnd, booking, zone)
            );

            slots.add(new TimeSlotDto(
                    slotIndex,
                    slotStart.toString(),
                    slotEnd.toString(),
                    available
            ));
        }

        return slots;
    }


    // Retourne "true" si le slot chevauche un booking existant.
    private boolean overlapsBooking(
            LocalDate date,
            LocalTime slotStart,
            LocalTime slotEnd,
            Booking booking,
            ZoneId zone
    ) {
        Instant slotStartInstant =
                date.atTime(slotStart).atZone(zone).toInstant();

        Instant slotEndInstant =
                date.atTime(slotEnd).atZone(zone).toInstant();

        return slotStartInstant.isBefore(booking.getEndTime())
                && slotEndInstant.isAfter(booking.getStartTime());
    }
}

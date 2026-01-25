package com.powerme.dto;

import com.powerme.enums.BookingStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record BookingDto(
        Long id,
        // Snapshots (figés)
        String stationName,
        String stationAddress,
        BigDecimal hourlyRate,
        // Résa
        Instant startTime,
        Instant endTime,
        BigDecimal totalPrice,
        BookingStatus bookingStatus,
        Instant createdAt
) {}

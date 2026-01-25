package com.powerme.service.booking;

import com.powerme.dto.BookingCreateRequestDto;
import com.powerme.entity.Booking;
import java.util.List;

public interface BookingService {

    public List<Booking> getBookingsForUser(Long userId);

    public Booking getBookingForUser(Long bookingId, Long userId);

    public Booking createBooking(
            BookingCreateRequestDto req,
            Long userId
    );
}

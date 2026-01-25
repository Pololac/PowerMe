package com.powerme.controller;

import com.powerme.dto.BookingCreateRequestDto;
import com.powerme.dto.BookingDto;
import com.powerme.entity.Booking;
import com.powerme.mapper.BookingMapper;
import com.powerme.service.booking.BookingService;
import com.powerme.service.security.UserPrincipal;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    public BookingController(BookingService bookingService, BookingMapper bookingMapper) {
        this.bookingService = bookingService;
        this.bookingMapper = bookingMapper;
    }

    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    public List<BookingDto> getBookings(@AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal.getId();
        List<Booking> bookings = bookingService.getBookingsForUser(userId);
        return bookingMapper.toDto(bookings);
    }

    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    public BookingDto createBooking(
            @RequestBody @Valid BookingCreateRequestDto request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long userId = principal.getId();
        Booking booking = bookingService.createBooking(request, userId);
        return bookingMapper.toDto(booking);
    }

}

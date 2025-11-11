package com.powerme.exception;

public class BookingNotFoundException extends ServiceException {

    public BookingNotFoundException() {
        super("Booking not found.");
    }
}

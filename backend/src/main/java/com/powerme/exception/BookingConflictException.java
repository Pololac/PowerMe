package com.powerme.exception;

public class BookingConflictException extends ServiceException {

    private final Object details;

    public BookingConflictException(String message, Object details) {
        super(message);
        this.details = details;
    }

    public Object getDetails() {
        return details;
    }
}

package com.powerme.exception;

public class BookingConflictException extends ServiceException {

    public BookingConflictException() {
        super("Un ou plusieurs créneaux sont déjà réservés");
    }

}

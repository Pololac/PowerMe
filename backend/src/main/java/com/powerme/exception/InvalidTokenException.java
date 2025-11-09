package com.powerme.exception;

public class InvalidTokenException extends ServiceException {

    public InvalidTokenException() {
        super("Invalid token");
    }
}

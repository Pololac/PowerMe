package com.powerme.exception;

public class ValidationException extends ServiceException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String field, String message) {
        super(field + ": " + message);
    }
}

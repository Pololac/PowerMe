package com.powerme.exception;

public class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
    }

    // Intéressant pour avoir l'exception technique (cause originale) en debug
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

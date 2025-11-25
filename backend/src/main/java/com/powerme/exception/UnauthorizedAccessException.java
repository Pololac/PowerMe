package com.powerme.exception;

/**
 * Erreur 403-Forbidden : accès interdit à cette ressource
 */
public class UnauthorizedAccessException extends ServiceException {

    public UnauthorizedAccessException() {
        super("Access denied to this resource");
    }

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}

package com.powerme.exception;

/**
 * Erreur 403-Forbidden : accès interdit à cette ressource
 */
public class UnauthorizedAccessException extends ServiceException {

    public UnauthorizedAccessException() {
        super("Accès à cette ressource interdit");
    }

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}

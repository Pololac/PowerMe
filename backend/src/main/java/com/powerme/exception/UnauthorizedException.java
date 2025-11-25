package com.powerme.exception;

/**
 * Erreur 401-Unauthorized : user introuvable ou token invalide
 */
public class UnauthorizedException extends ServiceException {

    public UnauthorizedException() {
        super("Authentication required");
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}

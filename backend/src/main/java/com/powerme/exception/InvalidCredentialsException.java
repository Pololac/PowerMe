package com.powerme.exception;

/**
 * Erreur 401-Unauthorized : user introuvable ou token invalide
 */
public class InvalidCredentialsException extends ServiceException {

    public InvalidCredentialsException() {
        super("Email ou mot de passe invalide");
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}

package com.powerme.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException() {
        super("Utilisateur déjà existant");
    }

    public UserAlreadyExistsException(String email) {
        super("Un utilisateur avec l'email " + email + " existe déjà");
    }
}

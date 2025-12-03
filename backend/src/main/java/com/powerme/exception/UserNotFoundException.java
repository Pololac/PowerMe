package com.powerme.exception;

public class UserNotFoundException extends ServiceException {

    public UserNotFoundException() {
        super("Utilisateur non trouvé");
    }

    public UserNotFoundException(Long userId) {
        super("Utilisateur avec l'ID " + userId + " non trouvé");
    }

    public UserNotFoundException(String identifier) {
        super("Utilisateur avec l'identifiant " + identifier + " non trouvé");
    }
}

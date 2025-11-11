package com.powerme.service.account;

import com.powerme.entity.User;


public interface AccountService {

    /**
     * Inscription : rôle par défaut, hash du mot de passe, persistance, génération + envoi d’un
     * token de validation par email.
     *
     * @param user Le nouvel User à faire persister
     * @return User persisté
     */
    User register(User user);

    /**
     * Validation d’un compte via token : décode le token, en extrait un User et active son compte
     * dans la base de données.
     */
    void activateAccount(String token);

    /**
     * Envoie un email avec un lien pour réinitialiser le mot de passe.
     *
     * @param email Le mail de la personne
     */
    void sendResetEmail(String email);

    /**
     * Reset le password du user non connecté.
     *
     * @param token       le token envoyé dans l'email de reset
     * @param newPassword le nouveau mot de passe à enregistrer
     */
    void resetPasswordWithToken(String token, String newPassword);

    /**
     * Reset le mot de passe d’un user authentifié
     *
     * @param user        Le User souhaitant modifier son mot de passe
     * @param newPassword Le nouveau mot de passe du User
     */
    void changePasswordAuthenticated(User user, String newPassword);

    /**
     * Suppression (soft delete) du compte utilisateur (RGPD).
     *
     * @param user Le User souhaitant supprimer son compte
     */
    void deleteAccount(User user);
}

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
     * Déclenche un reset password : envoie un lien avec un token permettant de créer un nouveau mot
     * de passe.
     *
     * @param email Le mail de la personne
     */
    void resetPassword(String email);

    /**
     * Modifie le mot de passe d’un user authentifié : hache le nouveau mot de passe du User et lui
     * assigne avant de le faire persister.
     *
     * @param user        Le User souhaitant modifier son mot de passe
     * @param newPassword Le nouveau mot de passe du User
     */
    void updatePassword(User user, String newPassword);

    /**
     * Suppression (soft delete) du compte utilisateur (RGPD).
     *
     * @param user Le User souhaitant supprimer son compte
     */
    void deleteAccount(User user);
}

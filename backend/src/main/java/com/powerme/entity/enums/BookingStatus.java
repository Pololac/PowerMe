package com.powerme.entity.enums;

/**
 * Statuts possibles d'une réservation.
 *
 * <p>Cycle de vie standard :</p>
 * <ul>
 *   <li>PENDING → ACCEPTED → COMPLETED</li>
 *   <li>PENDING → REFUSED</li>
 *   <li>PENDING/ACCEPTED → CANCELLED</li>
 * </ul>
 */
public enum BookingStatus {
    /**
     * Réservation créée, en attente de validation par le propriétaire.
     */
    PENDING("En attente de validation"),

    /**
     * Réservation acceptée par le propriétaire.
     *
     * <p>La borne est bloquée pour la période réservée.</p>
     */
    ACCEPTED("Acceptée"),

    /**
     * Réservation refusée par le propriétaire.
     */
    REFUSED("Refusée"),

    /**
     * Réservation terminée (date de fin dépassée).
     *
     * <p>Transition automatique depuis ACCEPTED.</p>
     */
    COMPLETED("Terminée"),

    /**
     * Réservation annulée par le client ou le propriétaire.
     */
    CANCELLED("Annulée");

    private final String displayName;

    BookingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

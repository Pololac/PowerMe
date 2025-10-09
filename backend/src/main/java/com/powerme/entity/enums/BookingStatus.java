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
    PENDING,

    /**
     * Réservation acceptée par le propriétaire.
     *
     * <p>La borne est bloquée pour la période réservée.</p>
     */
    ACCEPTED,

    /**
     * Réservation refusée par le propriétaire.
     */
    REFUSED,

    /**
     * Réservation terminée (date de fin dépassée).
     *
     * <p>Transition automatique depuis ACCEPTED.</p>
     */
    COMPLETED,

    /**
     * Réservation annulée par le client ou le propriétaire.
     */
    CANCELLED
}

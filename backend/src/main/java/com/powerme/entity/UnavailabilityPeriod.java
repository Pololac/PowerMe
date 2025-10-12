package com.powerme.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Période d'indisponibilité d'une borne (vacances, maintenance, etc.).
 */
@Entity
@Table(name = "unavailability_periods")
public class UnavailabilityPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ChargingStation chargingStation;


    // Constructeur
    public UnavailabilityPeriod() {
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Méthodes utilitaires

    /**
     * Vérifie si cette période d'indisponibilité chevauche une autre période.
     *
     * @param otherStart Début de l'autre période
     * @param otherEnd   Fin de l'autre période
     * @return true si les périodes se chevauchent
     */
    public boolean overlapsWith(LocalDateTime otherStart, LocalDateTime otherEnd) {
        // Chevauchement si: start < otherEnd ET end > otherStart
        return this.startTime.isBefore(otherEnd) && this.endTime.isAfter(otherStart);
    }


    // GETTERS / SETTERS
    public UUID getId() {
        return id;
    }

    public ChargingStation getChargingStation() {
        return chargingStation;
    }

    public void setChargingStation(ChargingStation chargingStation) {
        this.chargingStation = chargingStation;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

package com.powerme.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Borne de recharge électrique appartenant à un lieu de recharge.
 *
 * <p>Chaque borne possède un tarif horaire défini par le propriétaire et peut faire l'objet
 * de réservations par d'autres utilisateurs. Un type de prise donné peut délivrer plusieurs
 * puissances. Toutes les bornes sont équipées d'une prise Type 2S dans la v1 de l'application.
 * </p>
 */
@Entity
public class ChargingStation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    /**
     * Type de prise disponible sur cette borne.
     *
     * <p>Par défaut "Type 2S" selon le cahier des charges.
     * </p>
     */
    @Column(nullable = false)
    private String socketType = "Type 2S";

    /**
     * Puissance de la borne en kilowatts (kW).
     *
     * <p>Permet de différencier les bornes de même type mais de puissances différentes
     * (ex: 3,7 kW, 7 kW, 11 kW, 22 kW, 50 kW, etc.).
     * </p>
     */
    @Column(nullable = false)
    private Double power;

    /**
     * Tarif horaire de la borne défini par le propriétaire.
     *
     * <p>Montant en euros avec 2 décimales de précision.
     * </p>
     */
    @Column(nullable = false)
    private Double hourlyRate;

    /**
     * Indique si la borne est actuellement disponible à la réservation.
     */
    @Column(nullable = false)
    private Boolean isAvailable = true;

    /**
     * ChargingLocation "possède" ses ChargingStations. Le parent (Location) ne doit PAS être
     * supprimé par l'enfant (Station).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charging_location_id", nullable = false)
    private ChargingLocation chargingLocation;

    /**
     * Pas de CASCADE pour conserver l'historique des réservations passées pour une borne donnée.
     */
    @OneToMany(mappedBy = "chargingStation")
    private List<Booking> bookings = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public ChargingStation() {
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters et Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSocketType() {
        return socketType;
    }

    public void setSocketType(String socketType) {
        this.socketType = socketType;
    }

    public Double getPower() {
        return power;
    }

    public void setPower(Double power) {
        this.power = power;
    }

    public Double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(Double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public ChargingLocation getChargingLocation() {
        return chargingLocation;
    }

    public void setChargingLocation(ChargingLocation chargingLocation) {
        this.chargingLocation = chargingLocation;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}


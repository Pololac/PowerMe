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
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Représente une photo associée à un lieu de recharge ou une station.
 *
 * <p>Les photos sont stockées sur le serveur et seule le nom du fichier
 * est conservé en base de données.</p>
 */
@Entity
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Nom unique du fichier stocké.
     *
     * <p>Format recommandé : {@code {UUID}.{extension}}
     * Ex : {@code a3d7f891-4c2e-4f1b-9d3a-8f5e6c7d8e9f.jpg}
     * </p>
     */
    @Column(nullable = false, unique = true)
    private String filename;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charging_location_id")
    private ChargingLocation chargingLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charging_station_id")
    private ChargingStation chargingStation;

    // Lifecycle
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructeur
    public Photo() {
    }

    public Photo(ChargingLocation location, String filename) {
        this.chargingLocation = location;
        this.filename = filename;
    }

    public Photo(ChargingStation station, String filename) {
        this.chargingStation = station;
        this.filename = filename;
    }

    // Getters et Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ChargingLocation getChargingLocation() {
        return chargingLocation;
    }

    public void setChargingLocation(ChargingLocation chargingLocation) {
        this.chargingLocation = chargingLocation;
    }

    public ChargingStation getChargingStation() {
        return chargingStation;
    }

    public void setChargingStation(ChargingStation chargingStation) {
        this.chargingStation = chargingStation;
    }

}

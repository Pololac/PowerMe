package com.powerme.entity;

import jakarta.persistence.CascadeType;
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
 * Lieu de recharge contenant une ou plusieurs bornes électriques.
 *
 * <p>Chaque lieu appartient à un propriétaire unique et possède une localisation géographique.
 * La latitude/longitude sont dupliquées ici pour optimiser les recherches géographiques. Un lieu
 * peut contenir plusieurs bornes de recharge (ChargingStation).
 * </p>
 */
@Entity
public class ChargingLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    /**
     * Latitude GPS du lieu de recharge.
     *
     * <p>Dupliquée depuis Address pour optimiser les recherches géographiques.</p>
     */
    @Column(nullable = false)
    private Double latitude;

    /**
     * Longitude GPS du lieu de recharge.
     *
     * <p>Dupliquée depuis Address pour optimiser les recherches géographiques.</p>
     */
    @Column(nullable = false)
    private Double longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    /**
     * La suppression d'un lieu de recharge supprime ses bornes associées.
     */
    @OneToMany(mappedBy = "chargingLocation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChargingStation> chargingStations = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public ChargingLocation() {
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

    // Méthodes utilitaires

    /**
     * Ajoute une borne à ce lieu et maintient la bidirectionnalité.
     */
    public void addChargingStation(ChargingStation station) {
        chargingStations.add(station);
        station.setChargingLocation(this);
    }

    /**
     * Retire une borne de ce lieu et maintient la bidirectionnalité.
     */
    public void removeChargingStation(ChargingStation station) {
        chargingStations.remove(station);
        station.setChargingLocation(null);
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
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

    public List<ChargingStation> getChargingStations() {
        return chargingStations;
    }

    public void setChargingStations(List<ChargingStation> chargingStations) {
        this.chargingStations = chargingStations;
    }
}

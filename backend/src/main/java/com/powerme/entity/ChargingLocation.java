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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

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

    // COORDONNÉES GPS
    /**
     * Latitude GPS du lieu de recharge.
     *
     * <p>Déduite depuis Address pour optimiser les recherches géographiques.</p>
     */
    @Column(nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;

    /**
     * Longitude GPS du lieu de recharge.
     *
     * <p>Déduite depuis Address pour optimiser les recherches géographiques.</p>
     */
    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;

    /**
     * Point géographique PostGIS.
     */
    @Column(columnDefinition = "geography(Point, 4326)", nullable = false)
    private Point location;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Relations
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


    public ChargingLocation() {
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        createPoint();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        createPoint();
    }

    /**
     * Crée le point PostGIS à partir de latitude/longitude. Appelée automatiquement par @PrePersist
     * et @PreUpdate.
     */
    private void createPoint() {
        if (latitude != null && longitude != null) {
            GeometryFactory geometryFactory = new GeometryFactory();
            this.location = geometryFactory.createPoint(
                new Coordinate(longitude.doubleValue(), latitude.doubleValue())
            );
            this.location.setSRID(4326); // WGS84
        }
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

    /**
     * Définit les coordonnées GPS et met à jour le point PostGIS.
     *
     * @param latitude  latitude en degrés décimaux (-90 à 90)
     * @param longitude longitude en degrés décimaux (-180 à 180)
     */
    public void setCoordinates(BigDecimal latitude, BigDecimal longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        createPoint();
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

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
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

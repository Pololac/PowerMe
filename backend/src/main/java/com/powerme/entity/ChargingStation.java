package com.powerme.entity;

import com.powerme.entity.enums.ChargingPower;
import com.powerme.entity.enums.SocketType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Il est obligatoire de donner un nom à la station")
    @Column(nullable = false)
    private String name;

    /**
     * Type de prise de la borne (par défaut Type 2S).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,
            columnDefinition = "socket_type")   // Type PostgreSQL ENUM
    private SocketType connectorType = SocketType.TYPE_2S;

    /**
     * Puissance de la borne en kilowatts (kW).
     *
     * <p>Permet de différencier les bornes de même type mais de puissances différentes
     * (ex : 3,7 kW, 7,4 kW, 11 kW, 22 kW, 50 kW, etc.).
     * </p>
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,
            columnDefinition = "charging_power"  // Type PostgreSQL ENUM
    )
    private ChargingPower maxPower = ChargingPower.POWER_7_4;

    /**
     * Tarif horaire de la borne défini par le propriétaire.
     *
     * <p>Montant en euros</p>
     */
    @NotNull(message = "Tarif horaire obligatoire")
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal hourlyRate;

    /**
     * Indique si la borne est actuellement disponible à la réservation.
     */
    @Column(nullable = false)
    private Boolean isActive = true;

    /**
     * Heure de début de disponibilité quotidienne (ex : "08:00"). Si null, disponible 24h/24.
     */
    private LocalTime availableFrom; // Format HH:mm

    /**
     * Heure de fin de disponibilité quotidienne (ex : "20:00"). Si null, disponible 24h/24.
     */
    private LocalTime availableTo; // Format HH:mm

    // RELATIONS
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

    /**
     * Périodes de blocage (vacances, maintenance, etc.).
     */
    @OneToMany(mappedBy = "chargingStation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UnavailabilityPeriod> unavailabilityPeriods = new ArrayList<>();

    // Timestamps
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public ChargingStation() {
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Méthodes utilitaires

    /**
     * Permet d'obtenir la valeur numérique en kW.
     */
    public double getPowerInKilowatts() {
        return maxPower != null ? maxPower.getKilowatts() : 0.0;
    }

    /**
     * Ajoute une période d'indisponibilité à cette borne. Gère automatiquement la relation
     * bidirectionnelle.
     */
    public void addUnavailabilityPeriod(UnavailabilityPeriod period) {
        unavailabilityPeriods.add(period);
        period.setChargingStation(this);
    }

    /**
     * Retire une période d'indisponibilité de cette borne. Gère automatiquement la relation
     * bidirectionnelle.
     */
    public void removeUnavailabilityPeriod(UnavailabilityPeriod period) {
        unavailabilityPeriods.remove(period);
        period.setChargingStation(null);
    }


    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SocketType getConnectorType() {
        return connectorType;
    }

    public void setConnectorType(SocketType connectorType) {
        this.connectorType = connectorType;
    }

    public ChargingPower getMaxPower() {
        return maxPower;
    }

    public void setMaxPower(ChargingPower maxPower) {
        this.maxPower = maxPower;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalTime getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(LocalTime availableFrom) {
        this.availableFrom = availableFrom;
    }

    public LocalTime getAvailableTo() {
        return availableTo;
    }

    public void setAvailableTo(LocalTime availableTo) {
        this.availableTo = availableTo;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}


package com.powerme.entity;

import com.powerme.enums.BookingStatus;
import com.powerme.enums.ChargingPower;
import com.powerme.enums.SocketType;
import com.powerme.enums.StationStatus;
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
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "A name is required")
    @Column(nullable = false)
    private String name;

    private String imagePath;

    /**
     * Type de prise de la borne.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,
            columnDefinition = "socket_type")
    private SocketType socketType;

    /**
     * Puissance de la borne en kilowatts (kW).
     *
     * <p>Permet de différencier les bornes de même type mais de puissances différentes
     * (ex : 3,7 kW, 7,4 kW, 11 kW, 22 kW, 50 kW, etc.).
     * </p>
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "power", nullable = false)
    private ChargingPower power;

    /**
     * Tarif horaire de la borne défini par le propriétaire.
     *
     * <p>Montant en euros</p>
     */
    @NotNull(message = "Hourly rate is required")
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal hourlyRate;

    /**
     * Indique si la borne est actuellement disponible à la réservation. True by default
     */
    @Column(nullable = false)
    private boolean active = true;

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

    public ChargingStation() {     // Constructeur JPA

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

    // HELPERS METIER

    /**
     * Permet d'obtenir la valeur numérique en kW.
     */
    public double getPowerInKilowatts() {
        return power != null ? power.getKilowatts() : 0.0;
    }

    /**
     * Permet de savoir si une borne est utilisée en ce moment.
     */
    @Transient
    public boolean isReservedAt(Instant now) {
        return bookings.stream()
                .anyMatch(booking ->
                        booking.getBookingStatus() == BookingStatus.ACCEPTED
                                && !booking.getStartTime().isAfter(now)
                                && booking.getEndTime().isAfter(now)
                );
    }

    /**
     * Permet d'obtenir le status "réservable" de la borne en ce moment.
     */
    @Transient
    public StationStatus getStatus(Instant now) {

        if (!active) {
            return StationStatus.UNAVAILABLE;
        }

        if (isReservedAt(now)) {
            return StationStatus.OCCUPIED;
        }

        return StationStatus.AVAILABLE;
    }

    /**
     * Réactive la borne (car activée par défaut).
     */
    public void activate() {
        this.active = true;
    }

    /**
     * Désactive la borne.
     */
    public void deactivate() {
        this.active = false;
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

    public SocketType getSocketType() {
        return socketType;
    }

    public void setSocketType(SocketType socketType) {
        this.socketType = socketType;
    }

    public ChargingPower getPower() {
        return power;
    }

    public void setPower(ChargingPower power) {
        this.power = power;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public List<UnavailabilityPeriod> getUnavailabilityPeriods() {
        return unavailabilityPeriods;
    }

    public void setUnavailabilityPeriods(
            List<UnavailabilityPeriod> unavailabilityPeriods) {
        this.unavailabilityPeriods = unavailabilityPeriods;
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


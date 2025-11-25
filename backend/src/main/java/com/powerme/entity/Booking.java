package com.powerme.entity;

import com.powerme.enums.BookingStatus;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Représente une réservation de borne de recharge.
 *
 * <p>Une réservation lie un utilisateur à une borne pour une période donnée.
 * Le prix total est calculé automatiquement en fonction du tarif horaire et de la durée de
 * réservation.</p>
 *
 * <p>Cycle de vie : PENDING → ACCEPTED/REFUSED → COMPLETED/CANCELLED</p>
 */
@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "L'heure de début est obligatoire")
    @Column(nullable = false)
    private Instant startTime;

    @NotNull(message = "L'heure de fin est obligatoire")
    @Column(nullable = false)
    private Instant endTime;

    /**
     * Statut de la réservation.
     *
     * <p>Valeur par défaut : PENDING (en attente de validation du propriétaire).</p>
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,
            columnDefinition = "booking_status")  // Type PostgreSQL ENUM
    private BookingStatus bookingStatus = BookingStatus.PENDING;

    /**
     * Montant total de la réservation en euros.
     *
     * <p>Calculé automatiquement : tarif horaire × durée en heures.</p>
     */
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal totalPrice;

    // Timestamps
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    /**
     * Utilisateur qui effectue la réservation.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Borne de recharge réservée. Peut être NULL si borne supprimée.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charging_station_id")
    private ChargingStation chargingStation;

    // Snapshots : données figées de la borne (en cas de suppression future)
    @Column(name = "station_name_snapshot", nullable = false)
    private String stationNameSnapshot;

    @Column(name = "station_address_snapshot", nullable = false, columnDefinition = "TEXT")
    private String stationAddressSnapshot;

    @Column(name = "hourly_rate_snapshot", nullable = false, precision = 8, scale = 2)
    private BigDecimal hourlyRateSnapshot;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        captureStationSnapshot();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    /**
     * SNAPSHOT : Capture les infos de la borne au moment de la réservation. Ces données restent
     * même si la borne est supprimée.
     */
    private void captureStationSnapshot() {
        if (chargingStation != null) {
            this.stationNameSnapshot = chargingStation.getName();

            ChargingLocation location = chargingStation.getChargingLocation();
            if (location != null && location.getAddress() != null) {
                this.stationAddressSnapshot = location.getAddress().getFullAddress();
            }

            this.hourlyRateSnapshot = chargingStation.getHourlyRate();
        }
    }

    // HELPERS
    // Helper pour calculer le prix total
    private void calculateTotalPrice() {
/*        if (startTime != null && endTime != null && hourlyRateSnapshot != null) {
            long durationInHours = Duration.between(startTime, endTime).toHours();
            this.totalPrice = hourlyRateSnapshot.multiply(BigDecimal.valueOf(durationInHours));
        }*/
    }

    // Helper pour afficher le nom de la borne
    public String getDisplayStationName() {
        // Si la borne existe toujours, affiche son nom actuel
        if (chargingStation != null) {
            return chargingStation.getName();
        }
        // Sinon, affiche le snapshot avec une indication
        return stationNameSnapshot + " (borne supprimée)";
    }

    // Helper pour afficher l'adresse de la borne
    public String getDisplayStationAddress() {
        if (chargingStation != null &&
                chargingStation.getChargingLocation() != null &&
                chargingStation.getChargingLocation().getAddress() != null) {
            return chargingStation.getChargingLocation().getAddress().getFullAddress();
        }
        return stationAddressSnapshot;
    }

    // Constructeur
    public Booking() {
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ChargingStation getChargingStation() {
        return chargingStation;
    }

    public void setChargingStation(ChargingStation chargingStation) {
        this.chargingStation = chargingStation;
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

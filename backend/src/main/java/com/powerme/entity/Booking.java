package com.powerme.entity;

import com.powerme.entity.enums.BookingStatus;
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
import java.time.LocalDateTime;

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
    private LocalDateTime startTime;

    @NotNull(message = "L'heure de fin est obligatoire")
    @Column(nullable = false)
    private LocalDateTime endTime;

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
     * Borne de recharge réservée.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charging_station_id", nullable = false)
    private ChargingStation chargingStation;


    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
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

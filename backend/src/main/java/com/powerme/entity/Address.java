package com.powerme.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente les adresses des utilisateurs et des stations de recharge.
 */
@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Street address is required")
    @Column(nullable = false)
    private String streetAddress;

    @NotBlank(message = "Town is required")
    @Column(nullable = false)
    private String city;

    @NotBlank(message = "Postal code is required")
    @Column(nullable = false)
    private String postalCode;

    @NotBlank(message = "Country is required")
    @Column(nullable = false)
    private String country;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    // Relations
    /**
     * Pas de cascade pour éviter des suppressions problématiques.
     */
    @OneToMany(mappedBy = "address")
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "address")
    private List<ChargingLocation> chargingLocations = new ArrayList<>();

    // Constructeur JPA
    public Address() {
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

    // Méthode utilitaire
    public String getFullAddress() {
        return streetAddress + ", " + postalCode + " " + city;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<ChargingLocation> getChargingLocations() {
        return chargingLocations;
    }

    public void setChargingLocations(List<ChargingLocation> chargingLocations) {
        this.chargingLocations = chargingLocations;
    }
}

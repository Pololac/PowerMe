package com.powerme.entity;

import com.powerme.entity.enums.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Utilisateur de la plateforme PowerMe.
 *
 * <p>Peut être propriétaire de stations de recharge et/ou effectuer des réservations.
 * L'activation du compte nécessite la validation d'un code à six chiffres envoyé par email.
 * </p>
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    private String firstname;
    private String lastname;
    private String phone;
    private LocalDate birthday;
    private String avatarUrl;

    /**
     * Indique si le compte a été activé via le code de validation.
     *
     * <p>Un compte non activé ne peut pas accéder à toutes les fonctionnalités de la plateforme.
     * </p>
     */
    @Column(nullable = false)
    private Boolean isActivated = false;

    // Timestamps
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    /**
     * La suppression d'un utilisateur supprimera aussi ses lieux de recharges.
     */
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChargingLocation> chargingLocations = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserActivation> activations = new ArrayList<>();

    // Constructeurs
    public User() {
        this.roles.add(Role.ROLE_USER);
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
     * Pour ajouter un rôle à un utilisateur.
     */
    public void addRole(Role role) {
        this.roles.add(role);
    }

    /**
     * Pour enlever un rôle à un utilisateur.
     */
    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    /**
     * Pour savoir si un utilisateur a un rôle donné.
     */
    public boolean hasRole(Role role) {
        return this.roles.contains(role);
    }

    /**
     * Pour activer le compte utilisateur après validation du code.
     */
    public void activate() {
        this.isActivated = true;
    }

    /**
     * Pour supprimer un utilisateur sans l'effacer complètement.
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * Pour savoir si l'utilisateur est supprimé.
     *
     * @return true si l'utilisateur est bien supprimé
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    /**
     * Pour récupérer le nom complet de l'utilisateur.
     *
     * @return prénom + nom (ex: "John Doe")
     */
    public String getFullName() {
        if (firstname == null && lastname == null) {
            return email; // Fallback sur l'email
        }
        if (firstname == null) {
            return lastname;
        }
        if (lastname == null) {
            return firstname;
        }
        return firstname + " " + lastname;
    }

    // Getters & Setters
    public UUID getId() {
        return this.id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return this.roles;
    }

    public void setRoles(final Set<Role> roles) {
        this.roles = roles;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public LocalDate getBirthday() {
        return this.birthday;
    }

    public void setBirthday(final LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    public void setAvatarUrl(final String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Boolean isActivated() {
        return isActivated;
    }

    public void setActivated(final Boolean activated) {
        this.isActivated = activated;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(final LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return this.deletedAt;
    }

    public void setDeletedAt(final LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public List<UserActivation> getActivations() {
        return this.activations;
    }

    public void setActivations(final List<UserActivation> activations) {
        this.activations = activations;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(final Address address) {
        this.address = address;
    }

    public List<ChargingLocation> getChargingLocations() {
        return this.chargingLocations;
    }

    public void setChargingLocations(final List<ChargingLocation> chargingLocations) {
        this.chargingLocations = chargingLocations;
    }

    public List<Booking> getBookings() {
        return this.bookings;
    }

    public void setBookings(final List<Booking> bookings) {
        this.bookings = bookings;
    }

}

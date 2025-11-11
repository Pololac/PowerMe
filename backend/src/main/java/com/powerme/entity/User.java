package com.powerme.entity;

import com.powerme.enums.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Utilisateur de la plateforme PowerMe.
 *
 * <p>Peut être propriétaire de stations de recharge et/ou effectuer des réservations.
 * L'activation du compte nécessite la validation d'un code à six chiffres envoyé par email.
 * </p>
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Column(nullable = false)
    private String password;

    /**
     * Un utilisateur peut avoir plusieurs rôles (USER, ADMIN, OWNER). Ces rôles sont stockés dans
     * une Enum. JPA/Hibernate doit stocker cette relation "ManyToMany" dans une table de liaison.
     * {@code @ElementCollection} permet de créer automatiquement cette table de liaison.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "role", nullable = false, columnDefinition = "user_role")
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
    private boolean isActivated = false;

    // Timestamps
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    private Instant deletedAt;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    /**
     * Pas de cascade par défaut afin d'éviter les suppressions involontaires. La logique métier
     * sera gérée côté service.
     */
    @OneToMany(mappedBy = "owner")
    private List<ChargingLocation> chargingLocations = new ArrayList<>();

    /**
     * Pas de cascade par défaut afin d'éviter les suppressions involontaires. La logique métier
     * sera gérée côté service.
     */
    @OneToMany(mappedBy = "user")
    private List<Booking> bookings = new ArrayList<>();

    /**
     * Dépend totalement de User => cascade OK.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserActivation> activations = new ArrayList<>();

    // Constructeurs
    public User() {
        this.roles.add(Role.ROLE_USER);
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

    // Soft delete

    /**
     * Pour supprimer un utilisateur sans l'effacer complètement.
     */
    public void softDelete() {
        this.deletedAt = Instant.now();
    }

    /**
     * Pour savoir si l'utilisateur est supprimé.
     *
     * @return true si l'utilisateur est bien supprimé
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
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
    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
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

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(final boolean activated) {
        this.isActivated = activated;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(final Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(final Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getDeletedAt() {
        return this.deletedAt;
    }

    public void setDeletedAt(final Instant deletedAt) {
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

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .toList();
    }

    @Override
    public boolean isEnabled() {
        return isActivated;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isDeleted();
    }
}

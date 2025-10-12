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
 * Code d'activation à six chiffres pour la validation d'un compte utilisateur.
 *
 * <p>Expire après 24 heures et ne peut être utilisé qu'une seule fois.
 * Un utilisateur peut avoir plusieurs codes (en cas de renvoi), mais un seul est valide à la fois.
 * </p>
 */
@Entity
public class UserActivation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Code d'activation du compte envoyé par email.
     *
     * <p>Format: 6 chiffres (ex: 123 456)
     * </p>
     */
    @Column(nullable = false, unique = true)
    private Integer code;

    /**
     * Date limite de validité du code.
     *
     * <p>Par défaut : createdAt + 24 heures
     * </p>
     */
    @Column(nullable = false)
    private LocalDateTime expirationDate;

    /**
     * Indique si le code a déjà été utilisé.
     *
     * <p>Un code utilisé ne peut plus être réutilisé, même s'il n'est pas expiré.
     * </p>
     */
    @Column(nullable = false)
    private Boolean isUsed = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Constructeur
    public UserActivation() {
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Méthodes utilitaires

    /**
     * Vérifie si le code est expiré.
     *
     * @return true si la date d'expiration est dépassée
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationDate);
    }

    /**
     * Vérifie si le code est valide pour activer un compte.
     *
     * <p>Un code est valide s'il n'a pas été utilisé et n'est pas expiré.
     * </p>
     *
     * @return true si le code peut être utilisé
     */
    public boolean isValid() {
        return !isUsed && !isExpired();
    }

    /**
     * Marque le code comme utilisé après validation du compte.
     */
    public void markAsUsed() {
        this.isUsed = true;
    }

    // Getters & Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Boolean getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(Boolean isUsed) {
        this.isUsed = isUsed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

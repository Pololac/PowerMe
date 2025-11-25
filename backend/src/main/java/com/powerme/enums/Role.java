package com.powerme.enums;

/**
 * Rôles possibles des utilisateurs dans PowerMe. Un utilisateur peut avoir plusieurs rôles
 * simultanément.
 */
public enum Role {
    ROLE_USER,
    ROLE_ADMIN,
    ROLE_OWNER;

    // Helper pour Spring Security
    public String getAuthority() {
        return this.name();
    }
}

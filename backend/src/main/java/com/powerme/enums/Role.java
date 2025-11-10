package com.powerme.enums;

/**
 * Rôles possibles des utilisateurs.
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

package com.powerme.service.security;

import com.powerme.entity.User;
import com.powerme.enums.Role;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    // ═══════════════════════════════════════════════════════════
    // Constructeur privé utilisé par les factory methods
    // ═══════════════════════════════════════════════════════════

    private UserPrincipal(Long id, String email, String password,
            Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    // ═══════════════════════════════════════════════════════════
    // Factory methods
    // ═══════════════════════════════════════════════════════════

    /**
     * Crée un UserPrincipal depuis une entité User (utilisé au LOGIN) Utilisé par
     * CustomUserDetailsService
     */
    public static UserPrincipal fromUser(User user) {
        // Conversion des rôles
        Set<Role> roles = user.getRoles() != null
                ? user.getRoles()
                : Set.of();

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());

        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    /**
     * Crée un UserPrincipal depuis les claims JWT (utilisé à CHAQUE requête) Utilisé par
     * JwtService.validateAndLoadUser()
     */
    public static UserPrincipal fromJwtClaims(
            Long id,
            String email,
            String password,
            List<GrantedAuthority> authorities
    ) {
        return new UserPrincipal(id, email, password, authorities);
    }

    /**
     * Pour les tests d'intégration Crée un UserPrincipal minimal sans rôles
     */
    public static UserPrincipal fromActivationToken(String email) {
        return new UserPrincipal(
                null,                // id volontairement null
                email,
                null,          // pas de password
                Collections.emptyList() // pas de rôles
        );
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Set<String> getRoles() {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    // Méthodes UserDetails
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

}

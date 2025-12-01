package com.powerme.service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.powerme.exception.InvalidTokenException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Génère le JWT à partie des données de l'User. Retourne le UserDetails après validation du token.
 */
@Service
public class JwtService {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final JwtProperties props;

    public JwtService(JwtProperties props) {
        this.props = props;
        this.algorithm = Algorithm.HMAC512(props.getSecret());
        this.verifier = JWT.require(this.algorithm).build();
    }

    /**
     * Génère un token simple pour activation ou reset MdP (sans rôles)
     *
     * @param email      Email de l'user pour lequel on souhaite créer le token
     * @param expiration Date d'expiration
     * @return le token généré avec le temps d'expiration défini
     */
    public String generateToken(String email, Instant expiration) {
        return JWT.create()
                .withSubject(email)
                .withExpiresAt(expiration)
                .sign(algorithm);
    }

    /**
     * Génère un JWT complet pour l'authentification (avec userId + rôles)
     * Utilise l'expiration définie dans les properties.
     *
     * @param principal Le UserPrincipal authentifié
     * @return le JWT généré avec un temps d'expiration défini dans les props
     */
    public String generateJwt(UserPrincipal principal) {
        Instant expiration = Instant.now().plusSeconds(props.getAccessTokenExpiration());
        List<String> roles = principal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return JWT.create()
                .withSubject(principal.getUsername())
                .withClaim("userId", principal.getId())
                .withClaim("roles", roles)
                .withIssuedAt(Instant.now())
                .withExpiresAt(expiration)
                .sign(algorithm);
    }

    /**
     * Retourne le UserPrincipal à partir du JWT récupéré dans la requête.
     *
     * @param token le token en chaîne de caractères
     * @return le User lié au token
     */
    public UserPrincipal validateAndLoadUser(String token) {
        try {
            // Vérifie le token : pas expiré, valide, pas altéré ;
            // sinon "throw JWTVerificationException
            DecodedJWT decodedJwt = verifier.verify(token);

            // Reconstruit UserPrincipal depuis le payload du JWT (sans DB)
            String email = decodedJwt.getSubject();
            Long userId = decodedJwt.getClaim("userId").asLong();
            List<String> roles = decodedJwt.getClaim("roles").asList(String.class);

            List<GrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            return UserPrincipal.fromJwtClaims(userId, email, null, authorities);

        } catch (JWTVerificationException e) {
            // Token invalide ou expiré → transformation erreur 401
            throw new InvalidTokenException("Invalid or expired token") {
            };
        }
    }
}


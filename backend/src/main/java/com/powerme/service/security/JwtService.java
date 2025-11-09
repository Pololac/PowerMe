package com.powerme.service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.powerme.entity.User;
import java.time.Instant;
import java.util.stream.Collectors;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

/**
 * Génère le JWT à partie des données de l'User. Retourne le UserDetails après validation du token.
 */
@Service
public class JwtService {

    private final UserServiceImpl userService;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final JwtProperties props;

    public JwtService(UserServiceImpl userService, JwtProperties props) {
        this.userService = userService;
        this.props = props;
        this.algorithm = Algorithm.HMAC512(props.getSecret());
        this.verifier = JWT.require(this.algorithm).build();
    }

    /**
     * Génère un token contenant l'identifiant de l'utilisateur passé en paramètre.
     *
     * @param user       le User pour lequel on souhaite créer le JWT
     * @param expiration durée d'expiration
     * @return le token généré avec le temps d'expiration défini
     */
    public String generateToken(User user, Instant expiration) {
        return JWT.create()
                .withSubject(
                        user.getUsername())  // username = email du user en subject dans le payload
                .withExpiresAt(expiration)
                .withClaim("roles",
                        user.getRoles().stream()
                                .map(Enum::name)
                                .collect(Collectors.toList())
                )
                .sign(algorithm);
    }

    /**
     * Génère un JWT contenant l'identifiant de l'utilisateur passé en paramètre.
     *
     * @param user le User pour lequel on souhaite créer le JWT
     * @return le JWT généré avec un temps d'expiration défini dans les props
     */
    public String generateJwt(User user) {
        Instant expiration = Instant.now().plusSeconds(props.getAccessTokenExpiration());

        return generateToken(user, expiration);
    }

    /**
     * Vérifie la validité du token reçu et retourne le UserDetails correspondant.
     *
     * @param token le token en chaîne de caractères
     * @return le User lié au token
     */
    public User validateAndLoadUser(String token) {
        try {
            // Vérifie le token : pas expiré, valide, pas altéré...
            DecodedJWT decodedJwt = verifier.verify(token);

            // Récupère l'identifiant de l'User dans le payload
            String email = decodedJwt.getSubject();
            // On utilise le service pour récupérer le User en base de données (casté car UserDetails)
            User user = (User) userService.loadUserByUsername(email);

            // Vérifie que le compte est "enabled" (activé & non supprimé).
            if (!user.isEnabled()) {
                throw new AuthorizationDeniedException("User disabled") {
                };
            }

            return user;

        } catch (Exception e) {
            // En cas d'erreur de validation ou de chargement de l'User, on renvoie une erreur 403 Forbidden.
            throw new AuthorizationDeniedException("Error verifying token") {
            };
        }
    }
}


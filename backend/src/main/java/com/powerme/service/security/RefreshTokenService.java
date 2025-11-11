package com.powerme.service.security;

import com.powerme.entity.RefreshToken;
import com.powerme.entity.User;
import com.powerme.exception.InvalidTokenException;
import com.powerme.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Génère et persiste un refresh token pour un User donné.
 *
 * <p>Vérifie un refresh token et, si OK,
 * régénère une nouvelle paire de tokens et les renvoie. </p>
 *
 * <p>Supprime un refresh token donné.</p>
 *
 * <p>Supprime toutes les 24h les rt expirés.</p>
 */
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepo;
    private final JwtProperties props;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepo, JwtProperties props) {
        this.refreshTokenRepo = refreshTokenRepo;
        this.props = props;
    }

    /**
     * Crée et persiste un refresh token pour un utilisateur donné. Durée de vie : 30 jours.
     */
    public String create(User user) {
        RefreshToken rt = new RefreshToken();
        rt.setToken(UUID.randomUUID().toString());
        rt.setUser(user);
        rt.setExpiresAt(Instant.now().plusSeconds(props.getRefreshTokenExpiration()));
        refreshTokenRepo.save(rt);
        return rt.getToken();
    }

    /**
     * Valide le refresh et renvoie le User associé (utile pour générer un JWT côté Service).
     */
    public User validateAndGetUser(String token) {
        RefreshToken rt = refreshTokenRepo.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));

        // Vérifie qu'il n'est pas expiré
        if (rt.getExpiresAt().isBefore(Instant.now())) {
            // Si expiré, le supprime de la base et retourne une exception
            refreshTokenRepo.delete(rt);
            throw new InvalidTokenException("Refresh token expired");
        }

        return rt.getUser();
    }

    /**
     * Rotation : Valide le refresh token reçu, en crée un nouveau, supprime l'ancien et renvoie le
     * nouveau refresh token.
     */
    @Transactional
    public String rotate(String providedToken) {
        // Vérifie que le refresh token existe
        RefreshToken current = refreshTokenRepo.findByToken(providedToken)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token inconnu"));

        // Vérifie qu'il n'est pas expiré
        if (current.getExpiresAt().isBefore(Instant.now())) {
            // Si expiré, le supprime de la base et retourne une exception
            refreshTokenRepo.delete(current);
            throw new IllegalStateException("Refresh token expiré");
        }

        // Récupère l'utilisateur lié
        User user = current.getUser();
        // Génère un nouveau refresh token pour cet user
        String newRefresh = create(user);
        // Supprime l'ancien refresh token
        refreshTokenRepo.delete(current);

        // Retourne le refresh token
        return newRefresh;
    }

    /**
     * Supprime un refresh token donné (déconnexion ou invalidation).
     */
    public void deleteTokenFromBase(String token) {
        refreshTokenRepo.findByToken(token)
                .ifPresent(refreshTokenRepo::delete);
    }

    /**
     * Supprime tous les refresh token d'un user (suppression compte ou déconnexion de toutes ses
     * sessions sur différents supports).
     */
    public void deleteByUser(User user) {
        refreshTokenRepo.deleteByUser(user);
    }

    // Nettoyage de la table RefreshToken toutes les 24h
    @Transactional
    @Scheduled(fixedDelay = 24, timeUnit = TimeUnit.HOURS)
    void cleanExpiredTokens() {
        refreshTokenRepo.deleteExpiredToken();
    }
}


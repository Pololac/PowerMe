package com.powerme.service.security;

import com.powerme.dto.LoginCredentialsDto;
import com.powerme.dto.UserDto;
import com.powerme.entity.User;
import com.powerme.mapper.UserMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Se charge du login, refresh de tokens et du logout.
 */
@Service
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper mapper;

    public AuthService(AuthenticationManager authManager,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            UserMapper mapper) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.mapper = mapper;
    }

    public record LoginResult(String accessToken, String refreshToken, UserDto user) {

    }

    public record RefreshResult(String accessToken, String refreshToken) {

    }

    /**
     * Vérifie email + password via Spring Security. Génère un accessToken (JWT) & un refreshToken.
     */
    public LoginResult login(LoginCredentialsDto credentials) {
        // Vérifie credentials via Spring Security : récupère le User stocké en base
        // à partir de l'email fourni et vérifie que le hash du mdp stocké correspond
        // au mot de passe fourni ; si OK → construit un Authentication avec
        // le principal (user), ses rôles...
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        credentials.getEmail(),
                        credentials.getPassword()
                )
        );

        // Récupère le User authentifié et le "caste" en User
        User user = (User) auth.getPrincipal();

        // Génère un JWT pour ce user
        String accessToken = jwtService.generateJwt(user);
        // Génère et stocke en BDD un refresh token lié à ce user
        String refreshToken = refreshTokenService.create(user);

        // Mappe l'utilisateur vers un DTO safe
        UserDto userDto = mapper.toDto(user);

        // Retourne les tokens et le userDto (email, roles)
        return new LoginResult(accessToken, refreshToken, userDto);
    }

    /**
     * Le front envoie son refresh token actuel. Via le refreshTokenService, on vérifie qu'il est
     * encore valide et, si oui, on récupère l'user associé. A partir de ces infos, on génère des
     * nouveaux refresh/access tokens que l'on retourne.
     */
    public RefreshResult refreshTokens(String refreshToken) {
        User user = refreshTokenService.validateAndGetUser(refreshToken);
        String newAccess = jwtService.generateJwt(user);
        String newRefresh = refreshTokenService.rotate(refreshToken);
        return new RefreshResult(newAccess, newRefresh);
    }

    /**
     * Supprime le refresh token du client (invalidation côté serveur). L'access token côté client
     * expirera naturellement.
     */
    public void deleteRefresh(String refreshToken) {
        refreshTokenService.deleteTokenFromBase(refreshToken); // ou delete si tu supprimes
    }
}

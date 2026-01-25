package com.powerme.service.security;

import com.powerme.dto.LoginRequestDto;
import com.powerme.dto.UserDto;
import com.powerme.mapper.UserMapper;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

/**
 * Se charge du login, refresh de tokens et du logout.
 */
@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(AuthenticationManager authManager,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            UserMapper mapper) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public record LoginResult(String accessToken, String refreshToken, UserDto user) {

    }

    public record RefreshResult(String accessToken, String refreshToken) {

    }

    /**
     * Authentifie l'utilisateur et génère les tokens.
     *
     * @param request crédentials de login
     * @return LoginResult avec access token, refresh token et les infos du user
     * @throws BadCredentialsException si credentials invalides
     */
    public LoginResult login(LoginRequestDto request) {
        logger.info("Login attempt for user {}", request.email());

        try {
            // Authentification Spring Security : récupère le User en base avec l'email,
            // vérifie que hash du mdp stocké correspond au mdp fourni ;
            // si OK → construit un Authentication avec le principal, ses rôles...
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );

            // Récupère le UserPrincipal
            UserPrincipal principal = (UserPrincipal) auth.getPrincipal();

            // Génère les tokens de cet user
            String accessToken = jwtService.generateJwt(principal);
            String refreshToken = refreshTokenService.create(principal.getId());

            // Crée un UserDto local pour LOGIN
            UserDto userDto = new UserDto(
                    principal.getId(),
                    principal.getEmail(),
                    principal.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toSet())
            );

            // Retourne les tokens et le userDto (email, roles)
            return new LoginResult(accessToken, refreshToken, userDto);

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(
                    "Email ou mot de passe incorrect"
            );
        }
    }

    /**
     * Le front envoie son refresh token actuel. Via le refreshTokenService, on vérifie qu'il est
     * encore valide et, si oui, on récupère l'user associé. A partir de ces infos, on génère des
     * nouveaux refresh/access tokens que l'on retourne.
     */
    public RefreshResult refreshTokens(String refreshToken) {
        logger.info("Starting refresh token process");

        // Valide le refresh token et récupère le UserPrincipal
        UserPrincipal principal = refreshTokenService.validateAndGetPrincipal(refreshToken);
        // Génère un nouveau JWT
        String newAccess = jwtService.generateJwt(principal);

        // Rotate le refresh token (invalide l'ancien, crée un nouveau)
        String newRefresh = refreshTokenService.rotate(refreshToken);

        return new RefreshResult(newAccess, newRefresh);
    }

    /**
     * Supprime le refresh token du client (invalidation côté serveur). L'access token côté client
     * expirera naturellement.
     */
    public void deleteRefresh(String refreshToken) {
        logger.info("Starting refresh token deletion process");

        refreshTokenService.deleteTokenFromBase(refreshToken); // ou delete si tu supprimes
    }
}

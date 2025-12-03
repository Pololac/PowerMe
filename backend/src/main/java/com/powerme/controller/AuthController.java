package com.powerme.controller;

import com.powerme.dto.ErrorResponseDto;
import com.powerme.dto.LoginRequestDto;
import com.powerme.dto.LoginResponseDto;
import com.powerme.dto.RefreshResponseDto;
import com.powerme.dto.UserDto;
import com.powerme.exception.InvalidTokenException;
import com.powerme.service.security.AuthService;
import com.powerme.service.security.AuthService.LoginResult;
import com.powerme.service.security.AuthService.RefreshResult;
import com.powerme.service.security.UserPrincipal;
import jakarta.validation.Valid;
import java.time.Duration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur REST gérant l'authentification et la gestion des tokens JWT.
 *
 * <p>Expose les endpoints publics utilisés par le front-end pour :
 * <ul>
 *   <li>Authentifier un utilisateur et obtenir un couple {@code accessToken / refreshToken}</li>
 *   <li>Rafraîchir un access token expiré à l’aide d’un refresh token valide</li>
 *   <li>Déconnecter un utilisateur (invalidation du refresh token)</li>
 * </ul>
 *
 * <p>Tous les endpoints de ce contrôleur sont publics (accessibles sans JWT).
 */
@RestController
@RequestMapping("/api/auth")
// @CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthController {

    public static final String REFRESH_COOKIE = "refresh-token";
    public static final String REFRESH_COOKIE_PATH = "/api/auth";
    private final static boolean isProd = false;      // TODO: true en prod (HTTPS)
    private final static boolean isCrossSite = false; // TODO: true si front ≠ domaine API

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /auth/login: si credentials OK, renvoi access dans le body, refresh en cookie.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody @Valid LoginRequestDto request) {
        // Retourne access, refresh, user
        LoginResult result = authService.login(request);

        // On crée un cookie dans lequel on stocke le refresh token
        // (voir méthode en bas de la classe).
        ResponseCookie refreshCookie = generateCookie(result.refreshToken());

        // Dans la réponse, on envoie le refresh-token dans le cookie et le jwt dans le body.
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new LoginResponseDto(result.accessToken(), result.user()));
    }

    /**
     * GET /auth/me : retourne les infos de l'utilisateur connecté.
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal principal) {

        UserDto userDto = new UserDto(
                principal.getId(),
                principal.getEmail(),
                principal.getRoles()
        );

        return ResponseEntity.ok(userDto);    }

    /**
     * POST /auth/refresh : lit le refresh en cookie et renvoie une nouvelle paire de tokens.
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue(name = REFRESH_COOKIE, required = false) String refreshCookie) {
        if (refreshCookie == null || refreshCookie.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDto("MISSING_REFRESH_TOKEN", "Le refresh token est manquant"));
        }

        try {
            RefreshResult result = authService.refreshTokens(refreshCookie);
            ResponseCookie cookie = generateCookie(result.refreshToken());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(new RefreshResponseDto(result.accessToken()));

        } catch (InvalidTokenException e) {
            // refresh invalide/expiré/révoqué → on supprime le cookie
            ResponseCookie clear = clearRefreshCookie();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.SET_COOKIE, clear.toString())
                    .body(new ErrorResponseDto("INVALID_REFRESH_TOKEN", e.getMessage()));
        }
    }

    /**
     * POST /auth/logout : révoque le refresh et supprime le cookie.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = REFRESH_COOKIE, required = false) String refreshCookie) {
        if (refreshCookie != null && !refreshCookie.isBlank()) {
            authService.deleteRefresh(refreshCookie);
        }

        ResponseCookie clear = clearRefreshCookie();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, clear.toString())
                .build();
    }

    // --- Helpers cookie ---
    private ResponseCookie generateCookie(String refreshToken) {
        Duration maxAge = Duration.ofDays(30);    // aligné avec props.refreshTokenExpiration
        String sameSite = isCrossSite ? "None" : "Lax";

        return ResponseCookie.from(REFRESH_COOKIE, refreshToken)
                .httpOnly(
                        true) // Refresh token pas manipulable par le JS.
                .secure(isProd || isCrossSite)  // En ppe "true" → envoyé qu'en HTTPS.
                .sameSite(sameSite) // Cadre d'utilisation :
                .path(REFRESH_COOKIE_PATH) // Indique le chemin où envoyer le token.
                .maxAge(maxAge)
                .build();
    }

    private ResponseCookie clearRefreshCookie() {
        String sameSite = isCrossSite ? "None" : "Lax";
        return ResponseCookie.from(REFRESH_COOKIE, "")
                .httpOnly(true)
                .secure(isProd || isCrossSite)
                .sameSite(sameSite)
                .path(REFRESH_COOKIE_PATH)
                .maxAge(0)
                .build();
    }
}

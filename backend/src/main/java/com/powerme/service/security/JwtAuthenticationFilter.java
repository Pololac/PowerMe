package com.powerme.service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // On récupère le contenu du header Authorization où peut se trouver le token
        String authHeader = request.getHeader("Authorization");

        // Si routes liées à l'authentification / pas de header / pas de Bearer
        // alors on laisse passer sans rien faire
        if (request.getRequestURI().startsWith("/api/auth")
                || request.getRequestURI().startsWith("/api/account/register")
                || request.getRequestURI().startsWith("/api/account/activate")
                || request.getRequestURI().equals("/api/account/password/.*")
                || request.getRequestURI().startsWith("/api/charging-station")
                || request.getRequestURI().startsWith("/actuator/health")
                || authHeader == null
                || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Récupération du JWT et vérification s'il est légitime
        String token = authHeader.substring(7); // après "Bearer "
        try {
            // Valide le token et charge l'utilisateur
            UserDetails user = jwtService.validateAndLoadUser(token);

            // Place l'utilisateur dans le contexte de sécurité
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );
            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (AuthorizationDeniedException e) {
            // Token invalide / expiré / user désactivé
            response.sendError(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
        }
    }
}

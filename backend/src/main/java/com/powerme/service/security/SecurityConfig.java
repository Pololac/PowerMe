package com.powerme.service.security;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtAuthenticationFilter jwtFilter;
    private final CustomUserDetailsService userService;

    public SecurityConfig(
            AuthenticationConfiguration authenticationConfiguration,
            JwtAuthenticationFilter jwtFilter,
            CustomUserDetailsService userService
    ) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtFilter = jwtFilter;
        this.userService = userService;
    }

    // BCrypt pour hacher / vérifier les mots de passe
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Système d'authentification basé sur le UserDetailService et BCrypt
    // utilisé dans le AuthService.
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // injecté dans AuthService pour faire le login email/pwd
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Chaîne de filtrage et règles de sécurité
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF désactivé car API stateless
                .csrf(AbstractHttpConfigurer::disable)

                // CORS (règles définies ci-dessous) : désactivable en prod car même origine
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Filtrage routes publiques & privées
                .authorizeHttpRequests(auth -> auth
                        // Authentification
                        .requestMatchers("/api/auth/**").permitAll()

                        // Account
                        .requestMatchers("/api/account/register").permitAll()
                        .requestMatchers("/api/account/activate/**").permitAll()
                        .requestMatchers("/api/account/password/forgot/**").permitAll()
                        .requestMatchers("/api/account/password/reset").permitAll()
                        .requestMatchers("/api/account/password/change").authenticated()
                        .requestMatchers("/api/account/**").authenticated()

                        // Landing page
                        .requestMatchers(
                                "/public/landing",
                                "/public/content/**"
                        ).permitAll()

                        // HealthCheck
                        .requestMatchers("/actuator/health").permitAll()

                        // Carte / bornes consultables sans compte
                        .requestMatchers(HttpMethod.GET, "/api/charging-locations/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/charging-station/**").permitAll()

                        // Routes protégées
                        .requestMatchers("/api/booking/**").authenticated()
                        .requestMatchers("/api/owner/**")
                        .hasAnyRole("OWNER", "ADMIN") // Pas besoin de "ROLE_"

                        // Par défaut, protégées
                        .anyRequest().authenticated()
                )

                // Gère les 401 quand pas de token valide
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write("""
                                    {
                                        "title": "Authentication Required",
                                        "status": 401,
                                        "detail": "Full authentication is required to access this resource"
                                    }
                                    """);
                        })
                )

                // Pas de session serveur car JWT stateless
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Indique à Spring comment authentifier email/pwd
                .authenticationProvider(authenticationProvider())

                // Injecte le filtre JWT (qui lit le Bearer et peuple SecurityContext)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS pr dev : Autorise l'app Angular en local
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Proxy sur même serveur -> même origine = pas de restrictions CORS nécessaires
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("*"));    //
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

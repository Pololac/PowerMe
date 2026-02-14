package com.powerme.integration.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.powerme.config.AbstractIntegrationTest;
import com.powerme.entity.User;
import com.powerme.exception.InvalidTokenException;
import com.powerme.repository.UserRepository;
import com.powerme.service.mail.MailService;
import com.powerme.service.security.JwtService;
import com.powerme.service.security.RefreshTokenService;
import com.powerme.service.security.UserPrincipal;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc   // Utilisé pour simuler des requêtes HTTP
@Transactional  // Rollback automatique après chaque test
public class RegisterIT extends AbstractIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository userRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    MailService mailService;

    @MockitoBean
    RefreshTokenService refreshTokenService;

    @BeforeEach
    void cleanDatabase() {
        userRepo.deleteAll();
        userRepo.flush();
    }

    @Test
    void registerShouldSaveNewUserAndSendEmail() throws Exception {
        // GIVEN
        userRepo.save(new User("john@test.com", "hashedPassword"));
        userRepo.flush();
        when(jwtService.generateToken(eq("james@test.com"), any(Instant.class)))
                .thenReturn("fakeToken");

        // WHEN & THEN
        mvc.perform(post("/api/account/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "email": "james@test.com",
                                      "password": "12345678"
                                    }
                                """))
                .andExpect(status().isCreated());

        // THEN
        // Le nouvel user doit être en base
        assertTrue(userRepo.findByEmail("james@test.com").isPresent());

        // Un email est envoyé
        verify(mailService, times(1))
                .sendActivationEmail(any(User.class), any(String.class));
    }

    @Test
    void registerShouldReturnConflictWhenEmailExists() throws Exception {
        // GIVEN
        userRepo.save(new User("john@test.com", "hashedPassword"));
        userRepo.flush();

        long countBefore = userRepo.count();

        //WHEN & THEN
        // Vérifie que l'exception est levée (avec l'erreur 409) et le bon message envoyé
        mvc.perform(post("/api/account/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "email": "john@test.com",
                                      "password": "12345678"
                                    }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Utilisateur déjà existant"))
                .andExpect(jsonPath("$.detail").value(
                        "Un utilisateur avec l'email john@test.com existe déjà"));

        // THEN
        // Aucun user ajouté à la base
        long countAfter = userRepo.count();
        assertEquals(countBefore, countAfter);

        // Aucun mail envoyé
        verify(mailService, times(0))
                .sendActivationEmail(any(), any());
    }

    @Test
    void activateLinkShouldActiveUSerAccount() throws Exception {
        // GIVEN
        User john = new User("john@test.com", "hashedPassword");
        userRepo.save(john);
        userRepo.flush();

        User existing = userRepo.findByEmail("john@test.com").orElseThrow();
        // Vérification de l'état initial
        assertFalse(existing.isActivated(), "User should not be activated initially");

        String token = "validation.token";
        // Mock retourne UserPrincipal avec l'ID de l'user
        UserPrincipal mockPrincipal = UserPrincipal.fromActivationToken(
                existing.getEmail()
        );
        when(jwtService.validateActivationToken(token)).thenReturn(mockPrincipal);

        // WHEN & THEN
        mvc.perform(get("/api/account/activate/" + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Compte activé."));

        // THEN
        User activated = userRepo.findByEmail(existing.getEmail()).orElseThrow();
        assertTrue(activated.isActivated(), "User should be activated by the endpoint");
    }

    @Test
    void activateLinkShouldReturnErrorWhenTokenExpired() throws Exception {
        // GIVEN
        String expiredToken = "expired.token";
        when(jwtService.validateActivationToken(expiredToken))
                .thenThrow(new InvalidTokenException("Token invalide ou expiré"));

        // WHEN & THEN
        mvc.perform(get("/api/account/activate/" + expiredToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Token Invalide"))
                .andExpect(jsonPath("$.detail").value("Token invalide ou expiré"));
    }

    @Test
    void resetPasswordShouldResetPasswordWhenTokenIsValid() throws Exception {
        // GIVEN
        User user = new User("john@test.com", "oldPassword");
        userRepo.save(user);
        userRepo.flush();
        // Un token valide renverra ce user
        UserPrincipal mockPrincipal = UserPrincipal.fromUser(user);
        when(jwtService.validateActivationToken("valid.token")).thenReturn(mockPrincipal);
        String newPassword = "12345678";

        // WHEN & THEN
        mvc.perform(post("/api/account/password/reset")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                   {
                                     "token":"valid.token",
                                     "newPassword":"12345678"
                                   }
                                """))
                .andExpect(status().isOk());

        // THEN
        User updated = userRepo.findByEmail("john@test.com").orElseThrow();

        // Le password doit être différent
        assertNotEquals("oldPassword", updated.getPassword());

        // Le password doit être haché par BCrypt
        assertTrue(updated.getPassword().startsWith("$2"), "Password should be a BCrypt hash");

        // Vérifie que le hash correspond bien au nouveau mdp
        assertTrue(passwordEncoder.matches(newPassword, updated.getPassword()));

        // Vérifie que la méthode delete du RefreshTokenService est bien appelée
        verify(refreshTokenService, times(1)).deleteByUserId(user.getId());
    }

    @Test
    void changePasswordShouldWorkWhenAuthenticated() throws Exception {
        User user = new User("john@test.com", "oldPassword");
        userRepo.save(user);
        userRepo.flush();

        UserPrincipal principal = UserPrincipal.fromUser(user);

        mvc.perform(post("/api/account/password/change")
                        .with(csrf())
                        .with(user(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                 "token":"ignored",
                                 "newPassword":"12345678"
                                 }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void changePasswordShouldReturn401WhenNotAuthenticated() throws Exception {
        mvc.perform(post("/api/account/password/change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                 "token":"ignored",
                                 "newPassword":"12345678"
                                 }
                                """))
                .andExpect(status().isUnauthorized());
    }


}

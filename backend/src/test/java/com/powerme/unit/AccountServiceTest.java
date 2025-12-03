package com.powerme.unit;

import static com.powerme.enums.Role.ROLE_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.powerme.entity.User;
import com.powerme.exception.UserAlreadyExistsException;
import com.powerme.repository.UserRepository;
import com.powerme.service.account.AccountServiceImpl;
import com.powerme.service.mail.MailService;
import com.powerme.service.security.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private MailService mailService;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void registerShouldSaveUser() {
        // GIVEN
        User user = new User("john@test.com", "12345678");
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("hashedPassword");
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        accountService.register(user);

        // THEN
        verify(userRepo, times(1)).save(assertArg(savedUser -> {
            assertEquals("john@test.com", savedUser.getEmail());
            assertEquals("hashedPassword", savedUser.getPassword());
            assertTrue(savedUser.hasRole(ROLE_USER));
        }));
    }

    @Test
    void registerShouldHashPassword() {
        User user = new User("john@test.com", "12345678");
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("hashed");
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        accountService.register(user);

        assertEquals("hashed", user.getPassword());
    }

    @Test
    void registerShouldGenerateTokenAndSendMail() {
        // GIVEN
        User user = new User("john@test.com", "12345678");
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("hashed");
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken(eq("john@test.com"), any())).thenReturn("access.token");

        // WHEN
        accountService.register(user);

        //THEN
        // Vérifie que le token a bien été généré
        verify(jwtService).generateToken(eq("john@test.com"), any());

        // Vérifie que l’email a été envoyé avec le token exact
        verify(mailService).sendActivationEmail(any(User.class), eq("access.token"));
    }

    @Test
    void registerShouldThrowExceptionWhenEmailExists() {
        // GIVEN
        User user = new User("john@test.com", "12345678");
        User existing = new User("john@test.com", "abcd");
        when(userRepo.findByEmail("john@test.com")).thenReturn(Optional.of(existing));

        // WHEN + THEN
        // Vérifie qu'une exception est levée
        assertThrows(
                UserAlreadyExistsException.class,
                () -> accountService.register(user)
        );

        //THEN
        // Vérifie que rien ne s'exécute
        verify(userRepo, never()).save(any());
        verify(jwtService, never()).generateToken(any(), any());
        verify(mailService, never()).sendActivationEmail(any(), any());
    }


    @Test
    void registerShouldNotSendEmailIfSaveFails() {
        // GIVEN
        User user = new User("john@test.com", "12345678");
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("hashed");
        // Simule une erreur lors de la sauvegarde en base
        when(userRepo.save(any()))
                .thenThrow(new RuntimeException("Database failure"));

        // WHEN + THEN
        assertThrows(RuntimeException.class, () -> accountService.register(user));

        //THEN
        // Vérifie qu'aucun token généré ni de mail envoyé
        verify(jwtService, never()).generateToken(any(), any());
        verify(mailService, never()).sendActivationEmail(any(), any());
    }
}

package com.powerme.service.account;

import com.powerme.entity.User;
import com.powerme.exception.UserAlreadyExistsException;
import com.powerme.exception.UserNotFoundException;
import com.powerme.repository.UserRepository;
import com.powerme.service.mail.MailService;
import com.powerme.service.security.JwtService;
import com.powerme.service.security.RefreshTokenService;
import com.powerme.service.security.UserPrincipal;
import com.powerme.utils.Sanitizer;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Se charge du register, de l'activation du compte, du reset/modify du password et du softDelete.
 */
@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final MailService mailService;

    public AccountServiceImpl(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            MailService mailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.mailService = mailService;
    }

    @Override
    public void register(User user) {
        logger.info("Registering user {}", user.getEmail());

        Optional<User> optUser = userRepository.findByEmail(user.getEmail());

        // Vérifie si le User n'existe pas déjà
        if (optUser.isPresent()) {
            throw new UserAlreadyExistsException(optUser.get().getEmail());
        }

        // Hache le password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Sauvegarde le nv User
        User savedUser = userRepository.save(user);

        // Génère un token d'activation (valable 7j).
        String token = jwtService.generateToken(savedUser.getEmail(),
                Instant.now().plus(7, ChronoUnit.DAYS));

        // Envoie ce token dans un lien cliquable au mail indiqué pour le User qu'on a persisté
        mailService.sendActivationEmail(savedUser, token);
    }

    @Override
    public void activateAccount(String token) {
        String sanitizedToken = Sanitizer.sanitizeInput(token);
        logger.info("Activating account with token {}", sanitizedToken);

        // Valide le token envoyé puis extrait le UserPrincipal
        // casté car le validateToken renvoie un UserDetails
        UserPrincipal principal = jwtService.validateActivationToken(token);

        // Charge l'utilisateur depuis la DB
        User user = userRepository.findByEmail(principal.getEmail())
                .orElseThrow(UserNotFoundException::new);

        user.setActivated(true);
        userRepository.save(user);
    }

    @Override
    public void sendResetEmail(String email) {
        String sanitizedEmail = Sanitizer.sanitizeInput(email);
        logger.info("Sending reset password email to user with email {}", sanitizedEmail);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        // Génère un token de reset (valable 1 heure)
        Instant expiration = Instant.now().plusSeconds(3600);
        String token = jwtService.generateToken(email, expiration);

        mailService.sendResetPasswordEmail(user, token);
    }

    @Transactional
    @Override
    public void resetPasswordWithToken(String token, String newPassword) {
        String sanitizedToken = Sanitizer.sanitizeInput(token);
        logger.info("Resetting password for user with token {}", sanitizedToken);

        // Vérifie d'abord que le token est OK ; puis que l'user inclus dans le token existe
        UserPrincipal principal = jwtService.validateActivationToken(token);

        // Charge l'utilisateur
        User user = userRepository.findByEmail(principal.getEmail())
                .orElseThrow(UserNotFoundException::new);

        // Encode le nv mdp avant de l'enregistrer
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(
                user); // Si erreur, GlobalExceptionHandler catche avec DataAccessException

        // On supprime tous les refresh tokens de l'User pour le forcer à se reconnecter
        // sur tous ses devices avec le nouveau MdP
        refreshTokenService.deleteByUserId(user.getId());
    }

    @Transactional
    @Override
    public void changePasswordAuthenticated(Long userId, String newPassword) {
        String sanitizedId = Sanitizer.sanitizeInput("userId");
        logger.info("Changing password for user with id {}", sanitizedId);

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (user.isDeleted()) {
            throw new UserNotFoundException("Cannot change password for deleted account");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(
                user); // Si erreur, GlobalExceptionHandler catche avec DataAccessException

        // On supprime tous les refresh tokens de l'User pour le forcer à se reconnecter
        // sur tous ses devices avec le nouveau MdP
        refreshTokenService.deleteByUserId(user.getId());
    }

    @Override
    @Transactional
    public void deleteAccount(Long userId) {
        logger.info("Deleting account for user with id {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        user.softDelete();
        userRepository.save(user);

        refreshTokenService.deleteByUserId(userId);
    }
}

package com.powerme.service.account;

import com.powerme.entity.User;
import com.powerme.exception.UserAlreadyExistsException;
import com.powerme.exception.UserNotFoundException;
import com.powerme.repository.UserRepository;
import com.powerme.service.mail.MailService;
import com.powerme.service.security.JwtService;
import com.powerme.service.security.RefreshTokenService;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Se charge du register, de l'activation du compte, du reset/modify du password et du softDelete.
 */
@Service
public class AccountServiceImpl implements AccountService {

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
    public User register(User user) {
        Optional<User> optUser = userRepository.findByEmail(user.getEmail());
        // Vérifie si le User n'existe pas déjà
        if (optUser.isPresent()) {
            throw new UserAlreadyExistsException();
        }

        // Hache le password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Sauvegarde le nv User
        User saved = userRepository.save(user);

        // Génère un token d'activation avec le JwtService (avec une expiration à 7j).
        String token = jwtService.generateToken(user, Instant.now().plus(7, ChronoUnit.DAYS));

        // Envoie ce token dans un lien cliquable au mail indiqué pour le User qu'on a persisté
        mailService.sendActivationEmail(user, token);

        return saved;
    }

    @Override
    public void activateAccount(String token) {
        // Valide le token envoyé puis extrait le User
        // casté car le validateToken renvoie un UserDetails
        User u = jwtService.validateAndLoadUser(token);
        u.setActivated(true);
        userRepository.save(u);
    }

    @Override
    public void resetPassword(String email) {
        // Vérifie que le User existe
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException());
        // Renvoie un token de reset valide 1h
        String token = jwtService.generateToken(u, Instant.now().plusSeconds(3600));
        mailService.sendResetPasswordEmail(u, token);
    }

    @Override
    @Transactional
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // On supprime tous les refresh tokens de l'User pour le forcer à se reconnecter
        // sur tous ses devices avec le nouveau MdP
        refreshTokenService.deleteByUser(user);
    }

    @Override
    @Transactional
    public void deleteAccount(User user) {
        user.softDelete();
        userRepository.save(user);
        refreshTokenService.deleteByUser(user);
    }
}

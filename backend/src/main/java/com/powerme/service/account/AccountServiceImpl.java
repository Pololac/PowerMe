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
    public void register(User user) {
        Optional<User> optUser = userRepository.findByEmail(user.getEmail());

        // Vérifie si le User n'existe pas déjà
        if (optUser.isPresent()) {
            throw new UserAlreadyExistsException(optUser.get().getEmail());
        }

        // Hache le password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Sauvegarde le nv User
        userRepository.save(user);

        // Génère un token d'activation avec le JwtService (avec une expiration à 7j).
        String token = jwtService.generateToken(user, Instant.now().plus(7, ChronoUnit.DAYS));

        // Envoie ce token dans un lien cliquable au mail indiqué pour le User qu'on a persisté
        mailService.sendActivationEmail(user, token);
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
    public void sendResetEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        String token = jwtService.generateToken(user, Instant.now().plusSeconds(3600));
        mailService.sendResetPasswordEmail(user, token);
    }

    @Override
    public void resetPasswordWithToken(String token, String newPassword) {
        // Vérifie que le User existe
        User u = jwtService.validateAndLoadUser(token);
        // Encode le nv mdp avant de l'enregistrer
        u.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(u);

        // On supprime tous les refresh tokens de l'User pour le forcer à se reconnecter
        // sur tous ses devices avec le nouveau MdP
        refreshTokenService.deleteByUser(u);
    }

    @Override
    @Transactional
    public void changePasswordAuthenticated(User user, String newPassword) {
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

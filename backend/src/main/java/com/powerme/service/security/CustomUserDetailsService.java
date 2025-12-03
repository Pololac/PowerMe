package com.powerme.service.security;

import com.powerme.entity.User;
import com.powerme.exception.UserNotFoundException;
import com.powerme.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service custom utilisé par Sping Security pour charger les "user details"
 * lors de l'authentification (uniquement au niveau du LOGIN).
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepo;

    public CustomUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Loading user by email: {}", email);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found: {}", email);
                    return new UsernameNotFoundException("Utilisateur non trouvé");
                });

        // Vérifie que le compte est bien activé
        if (!user.isActivated()) {
            throw new DisabledException("Account not activated");
        }

        // Vérifie que le compte n'est pas supprimé
        if (user.isDeleted()) {
            throw new UserNotFoundException("Account has been deleted: " + email);
        }

        return UserPrincipal.fromUser(user);
    }
}

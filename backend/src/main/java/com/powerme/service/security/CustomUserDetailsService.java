package com.powerme.service.security;

import com.powerme.entity.User;
import com.powerme.exception.UserNotFoundException;
import com.powerme.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service utilisé uniquement pour le LOGIN.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    public CustomUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));

        // Vérifie que le compte n'est pas supprimé
        if (user.isDeleted()) {
            throw new UserNotFoundException("Account has been deleted: " + email);
        }

        return UserPrincipal.fromUser(user);
    }
}

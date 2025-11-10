package com.powerme.repository;

import com.powerme.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Permet de contrôler si un User avec cet emil existe déjà en base.
     */
    Optional<User> findByEmail(String email);
}

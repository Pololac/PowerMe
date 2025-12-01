package com.powerme.repository;

import com.powerme.entity.RefreshToken;
import com.powerme.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    /**
     * Supprime tous les refresh tokens d'un utilisateur donné.
     * Peut servir pour un logout global ou lorsqu'un compte est supprimé.
     *
     * @param userId ID de l'utilisateur ciblé.
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = :userId")
    void deleteByUserId(Long userId);


    /**
     * Supprime tous les refresh tokens expirés en une seule requête SQL. Utilisable dans un job
     * planifié (ex : cron)
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < CURRENT_TIMESTAMP")
    void deleteExpiredToken();
}

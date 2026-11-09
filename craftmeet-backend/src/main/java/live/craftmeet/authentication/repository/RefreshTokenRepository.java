package live.craftmeet.authentication.repository;

import live.craftmeet.authentication.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing refresh token entities.
 * Provides persistence operations for refresh tokens through Spring Data JPA,
 * including lookup by token value and retrieval or deletion of all tokens
 * associated with a given user identifier. The repository is keyed by the
 * refresh token's UUID identifier.
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    @Query("SELECT r FROM RefreshToken r WHERE r.userId = :userId")
    List<RefreshToken> findByUserId(@Param("userId") UUID userId);

    @Query("DELETE FROM RefreshToken r WHERE r.userId = :userId")
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    void deleteByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.jti = :newJti WHERE r.userId = :userId AND r.jti = :oldJti")
    int rotate(UUID userId, UUID oldJti, UUID newJti);

    boolean existsByJti(UUID jti);
}

package live.craftmeet.authentication.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import live.craftmeet.authentication.model.VerifyEmailToken;

public interface VerifyEmailTokenRepository extends JpaRepository<VerifyEmailToken, UUID> {

    Optional<VerifyEmailToken> findByTokenHash(String tokenHash);

    Optional<VerifyEmailToken> findByUserId(UUID userId);

    @Query("DELETE FROM VerifyEmailToken r WHERE r.userId = :userId")
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    void deleteByUserId(@Param("userId") UUID userId);

}

package live.craftmeet.authentication.service;

import jakarta.transaction.Transactional;
import live.craftmeet.authentication.repository.RefreshTokenRepository;
import live.craftmeet.authentication.model.RefreshToken;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service that manages refresh token identifiers for authenticated users.
 * It creates a new refresh token for a user while removing previously stored
 * tokens, clears all refresh tokens associated with a user, and rotates an
 * existing token identifier to a new value. Persistence and token record
 * updates are delegated to the refresh token repository.
 */
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Creates a new refresh token for the user identified by the given user ID.
     * Any existing refresh tokens associated with the user are removed before
     * a new token identifier is generated and persisted.
     *
     * @param userId the identifier of the user for whom the refresh token is
     *               created
     * @return the generated identifier of the newly created refresh token
     */
    @Transactional
    public UUID create(UUID userId) {
        clear(userId);

        UUID jti = UUID.randomUUID();

        RefreshToken rt = new RefreshToken();
        rt.setJti(jti);
        rt.setUserId(userId);
        refreshTokenRepository.save(rt);
        return jti;
    }

    /**
     * Deletes all refresh token records associated with the user identified by
     * the given user ID.
     *
     * @param userId the identifier of the user whose refresh tokens are to be
     *               deleted
     */
    @Transactional
    public void clear(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    /**
     * Rotates the tracked refresh token identifier for the specified user by
     * replacing
     * the existing token identifier with the new token identifier.
     *
     * @param userId the identifier of the user whose refresh token is rotated
     * @param oldJti the existing refresh token identifier to replace
     * @param newJti the new refresh token identifier to assign
     * @return the number of affected refresh token records
     */
    @Transactional
    public int rotate(UUID userId, UUID oldJti, UUID newJti) {
        return refreshTokenRepository.rotate(userId, oldJti, newJti);
    }
}

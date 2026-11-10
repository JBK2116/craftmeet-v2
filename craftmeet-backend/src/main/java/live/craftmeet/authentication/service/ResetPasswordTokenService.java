package live.craftmeet.authentication.service;

import live.craftmeet.authentication.token.CooldownTokenException;
import live.craftmeet.authentication.token.InvalidTokenException;
import live.craftmeet.authentication.token.TokenType;
import live.craftmeet.user.exception.UserNotFoundException;
import live.craftmeet.user.exception.UserNotVerifiedException;
import live.craftmeet.user.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import live.craftmeet.authentication.model.ResetPasswordToken;
import live.craftmeet.authentication.repository.ResetPasswordTokenRepository;
import live.craftmeet.crypto.CryptoService;
import live.craftmeet.user.repository.UserRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

// This service should also inject a emailservice to send the reset password token to the user.
// For now it is not implemented, but it should be added in the future.

/**
 * Service responsible for managing password reset tokens.
 * Generates reset tokens for verified users, enforcing the configured cooldown
 * between token requests, and validates raw reset tokens by hashing them with
 * the configured secret and checking expiration and user state.
 */
@Service
public class ResetPasswordTokenService {

    private final CryptoService cryptoService;
    private final UserRepository userRepository;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final long resetTokenExpiration;
    private final long resetTokenCooldown;
    private final String resetTokenSecret;

    /**
     * Creates a password-reset token service with its persistence, cryptographic,
     * and token-lifetime dependencies.
     *
     * @param cryptoService                service used to generate and hash tokens
     * @param userRepository               repository used to look up users
     * @param resetPasswordTokenRepository repository used to persist reset tokens
     * @param resetTokenExpiration         token lifetime in milliseconds
     * @param resetTokenCooldown           minimum interval between token
     *                                     generations in milliseconds
     * @param resetTokenSecret             Base64-encoded secret used to hash tokens
     */
    public ResetPasswordTokenService(CryptoService cryptoService, UserRepository userRepository,
            ResetPasswordTokenRepository resetPasswordTokenRepository,
            @Value("${reset.token.expiration}") long resetTokenExpiration,
            @Value("${reset.token.cooldown}") long resetTokenCooldown,
            @Value("${reset.token.secret}") String resetTokenSecret) {
        this.cryptoService = cryptoService;
        this.userRepository = userRepository;
        this.resetPasswordTokenRepository = resetPasswordTokenRepository;
        this.resetTokenExpiration = resetTokenExpiration;
        this.resetTokenCooldown = resetTokenCooldown;
        this.resetTokenSecret = resetTokenSecret;
    }

    /**
     * Generates a new password reset token for the user identified by the given
     * email address.
     * The user must exist and must be verified. If an existing reset token is
     * present, its cooldown is checked; a new token is created only after the
     * cooldown has elapsed, and the previous token is removed.
     *
     * @param email the email address of the user for whom the reset token is
     *              generated
     * @return the newly generated raw password reset token
     * @throws UserNotFoundException    if no user exists with the given email
     *                                  address
     * @throws UserNotVerifiedException if the user's email address is not verified
     * @throws CooldownTokenException   if an existing token is still within the
     *                                  configured cooldown period
     */
    public String generate(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        if (!user.isVerified()) {
            throw new UserNotVerifiedException(email);
        }

        Optional<ResetPasswordToken> existingToken = resetPasswordTokenRepository.findByUserId(user.getId());
        if (existingToken.isPresent()) {
            ResetPasswordToken token = existingToken.get();
            if (!isCooledDown(token)) {
                throw new CooldownTokenException(TokenType.RESET_PASSWORD, getRemainingCooldownMS(token));
            }
            resetPasswordTokenRepository.delete(token);
            return createAndSave(user.getId());
        }
        return createAndSave(user.getId());

    }

    /**
     * Validates the supplied raw password reset token.
     * The raw token is hashed and looked up; validation fails if no matching
     * token exists, if the associated user cannot be found or is not verified,
     * or if the token is expired.
     *
     * @param rawToken the raw password reset token to validate
     * @throws InvalidTokenException if the token is unknown, expired, or its user
     *                               is invalid
     */
    public void validate(String rawToken) {
        String tokenHash = cryptoService.hashToken(rawToken, resetTokenSecret);
        ResetPasswordToken existingToken = resetPasswordTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidTokenException(TokenType.RESET_PASSWORD));

        User user = userRepository.findById(existingToken.getUserId())
                .orElseThrow(() -> new InvalidTokenException(TokenType.RESET_PASSWORD));

        // This case here should never happen realistically
        if (!user.isVerified()) {
            throw new InvalidTokenException(TokenType.RESET_PASSWORD);
        }

        if (isExpired(existingToken)) {
            throw new InvalidTokenException(TokenType.RESET_PASSWORD);
        }
        return;
    }

    /**
     * Creates and persists a reset token for the specified user.
     *
     * @param userId the identifier of the user associated with the token
     * @return the unhashed token value to deliver to the user
     */
    private String createAndSave(UUID userId) {
        String rawToken = cryptoService.generateRawToken();
        ResetPasswordToken token = new ResetPasswordToken();
        token.setUserId(userId);
        token.setTokenHash(cryptoService.hashToken(rawToken, resetTokenSecret));
        token.setExpiresAt(Instant.now().plusMillis(resetTokenExpiration));
        resetPasswordTokenRepository.save(token);
        return rawToken;
    }

    /**
     * Determines whether the token is past its expiration time.
     *
     * @param token the token whose expiration time is checked
     * @return {@code true} when the token can no longer be used
     */
    private boolean isExpired(ResetPasswordToken token) {
        return token.getExpiresAt().isBefore(Instant.now());
    }

    /**
     * Determines whether the cooldown period has elapsed since the token was
     * created.
     *
     * @param token the token whose creation time is checked
     * @return {@code true} when a new token may be generated
     */
    private boolean isCooledDown(ResetPasswordToken token) {
        return token.getCreatedAt().plusMillis(resetTokenCooldown).isBefore(Instant.now());
    }

    /**
     * Calculates the time remaining before another token may be generated.
     *
     * @param token the token whose cooldown is being measured
     * @return remaining cooldown in milliseconds
     */
    private int getRemainingCooldownMS(ResetPasswordToken token) {
        return (int) (token.getCreatedAt().plusMillis(resetTokenCooldown).toEpochMilli()
                - Instant.now().toEpochMilli());
    }

}

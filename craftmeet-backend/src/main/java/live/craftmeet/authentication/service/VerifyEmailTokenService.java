package live.craftmeet.authentication.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import live.craftmeet.authentication.model.VerifyEmailToken;
import live.craftmeet.authentication.repository.VerifyEmailTokenRepository;
import live.craftmeet.authentication.token.CooldownTokenException;
import live.craftmeet.authentication.token.InvalidTokenException;
import live.craftmeet.authentication.token.TokenType;
import live.craftmeet.crypto.CryptoService;
import live.craftmeet.user.exception.UserAlreadyVerifiedException;
import live.craftmeet.user.exception.UserNotFoundException;
import live.craftmeet.user.model.User;
import live.craftmeet.user.repository.UserRepository;

// TODO: This service should also inject a emailservice to send the verification token to the user.

/**
 * Service responsible for managing email verification tokens.
 * Generates verification tokens for users whose email is not yet verified,
 * enforcing the configured cooldown between token requests. Validates raw
 * verification tokens by hashing
 * them with the configured secret and checking expiration and user state before
 * marking the user's email as verified.
 */

@Service
public class VerifyEmailTokenService {
    public final CryptoService cryptoService;
    public final UserRepository userRepository;
    public final VerifyEmailTokenRepository verifyEmailRepository;
    public final long verifyTokenExpiration;
    public final long verifyTokenCooldown;
    public final String verifyTokenSecret;

    /**
     * Creates a verification-token service with its persistence, cryptographic,
     * and token-lifetime dependencies.
     *
     * @param cryptoService         service used to generate and hash tokens
     * @param userRepository        repository used to look up and update users
     * @param verifyEmailRepository repository used to persist verification tokens
     * @param verifyTokenExpiration token lifetime in milliseconds
     * @param verifyTokenCooldown   minimum interval between token generations in
     *                              milliseconds
     * @param verifyTokenSecret     Base64-encoded secret used to hash tokens
     */
    public VerifyEmailTokenService(CryptoService cryptoService, UserRepository userRepository,
            VerifyEmailTokenRepository verifyEmailRepository,
            @Value("${verify.token.expiration}") long verifyTokenExpiration,
            @Value("${verify.token.cooldown}") long verifyTokenCooldown,
            @Value("${verify.token.secret}") String verifyTokenSecret) {
        this.cryptoService = cryptoService;
        this.userRepository = userRepository;
        this.verifyEmailRepository = verifyEmailRepository;
        this.verifyTokenExpiration = verifyTokenExpiration;
        this.verifyTokenCooldown = verifyTokenCooldown;
        this.verifyTokenSecret = verifyTokenSecret;
    }

    /**
     * Generates a new email verification token for the user identified by the given
     * email address.
     * The user must exist and must not already be verified. If an existing
     * verification token is
     * present, its cooldown is checked; a new token is created only after the
     * cooldown has
     * elapsed, and the previous token is removed.
     *
     * @param email the email address of the user for whom the verification token is
     *              generated
     * @return the newly generated raw email verification token
     * @throws UserNotFoundException        if no user exists with the given email
     *                                      address
     * @throws UserAlreadyVerifiedException if the user's email address is already
     *                                      verified
     * @throws CooldownTokenException       if an existing token is still within the
     *                                      configured cooldown period
     */
    public String generate(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        if (user.isVerified()) {
            throw new UserAlreadyVerifiedException(user.getEmail());
        }

        Optional<VerifyEmailToken> existingToken = verifyEmailRepository.findByUserId(user.getId());
        if (existingToken.isPresent()) {
            VerifyEmailToken token = existingToken.get();
            if (!isCooledDown(token)) {
                throw new CooldownTokenException(TokenType.VERIFY_EMAIL, getRemainingCooldownMS(token));
            }
            verifyEmailRepository.delete(token);
        }
        return createAndSave(user.getId());
    }

    /**
     * Validates the supplied raw email verification token and, if valid, marks the
     * associated user's email as verified.
     * The raw token is hashed and looked up; validation fails if no matching token
     * exists or if
     * the token is expired. If the associated user cannot be found or is already
     * verified,
     * validation fails.
     * On success, the user's verified status and verification timestamp are updated
     * and persisted.
     *
     * @param rawToken the raw email verification token to validate
     * @throws InvalidTokenException        if the token is unknown, expired, or its
     *                                      user no longer exists
     * @throws UserAlreadyVerifiedException if the associated user's email is
     *                                      already verified
     */
    public void validate(String rawToken) {
        String tokenHash = cryptoService.hashToken(rawToken, verifyTokenSecret);
        VerifyEmailToken token = verifyEmailRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidTokenException(TokenType.VERIFY_EMAIL));

        if (isExpired(token)) {
            throw new InvalidTokenException(TokenType.VERIFY_EMAIL);
        }
        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new InvalidTokenException(TokenType.VERIFY_EMAIL));
        if (user.isVerified()) {
            throw new UserAlreadyVerifiedException(user.getEmail());
        }

        user.setVerified(true);
        user.setVerifiedAt(Instant.now());
        userRepository.save(user);
    }

    /**
     * Creates and persists a token for the specified user.
     *
     * @param userId the identifier of the user associated with the token
     * @return the unhashed token value to deliver to the user
     */
    private String createAndSave(UUID userId) {
        String rawToken = cryptoService.generateRawToken();
        VerifyEmailToken token = new VerifyEmailToken();
        token.setUserId(userId);
        token.setTokenHash(cryptoService.hashToken(rawToken, verifyTokenSecret));
        token.setExpiresAt(Instant.now().plusMillis(verifyTokenExpiration));
        verifyEmailRepository.save(token);
        return rawToken;
    }

    /**
     * Determines whether the cooldown period has elapsed since the token was
     * created.
     *
     * @param token the token whose creation time is checked
     * @return {@code true} when a new token may be generated
     */
    private boolean isCooledDown(VerifyEmailToken token) {
        return token.getCreatedAt().plusMillis(verifyTokenCooldown).isBefore(Instant.now());
    }

    /**
     * Determines whether the token is past its expiration time.
     *
     * @param token the token whose expiration time is checked
     * @return {@code true} when the token can no longer be used
     */
    private boolean isExpired(VerifyEmailToken token) {
        return token.getExpiresAt().isBefore(Instant.now());
    }

    /**
     * Calculates the time remaining before another token may be generated.
     *
     * @param token the token whose cooldown is being measured
     * @return remaining cooldown in milliseconds
     */
    private int getRemainingCooldownMS(VerifyEmailToken token) {
        return (int) (token.getCreatedAt().plusMillis(verifyTokenCooldown).toEpochMilli()
                - Instant.now().toEpochMilli());
    }

}

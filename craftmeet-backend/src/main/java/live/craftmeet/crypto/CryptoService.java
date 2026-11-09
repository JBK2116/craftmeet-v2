package live.craftmeet.crypto;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CryptoService {
    private final PasswordEncoder passwordEncoder;

    public CryptoService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Hashes a raw password using the configured {@link PasswordEncoder}.
     *
     * @param rawPassword the plaintext password to hash
     * @return the encoded password hash
     */
    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Verifies a raw password against a stored hash using the configured
     * {@link PasswordEncoder}.
     *
     * @param rawPassword    the plaintext password to verify
     * @param hashedPassword the stored encoded password to compare against
     * @return {@code true} if the password matches, otherwise {@code false}
     */
    public boolean checkPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    /**
     * Generates a cryptographically secure random token for use in
     * email-verification links.
     *
     * <p>
     * Produces 32 random bytes (256 bits of entropy) from
     * {@link java.security.SecureRandom} and encodes them as URL-safe Base64
     * without padding, so the result can be embedded directly in a URL.
     * </p>
     *
     * @return a URL-safe, unpadded Base64 string representing the random token
     */
    public String generateRawToken() {
        byte[] randomBytes = new byte[32];
        new java.security.SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    /**
     * Hashes a raw token using HMAC-SHA256 keyed with the provided secret.
     *
     * <p>
     * Used for both refresh tokens and email-verification tokens; each token type
     * uses its own secret. The secret is stored Base64-encoded and decoded here.
     * The resulting hash is returned Base64-encoded so it can be persisted and
     * later compared without exposing the raw token. A keyed hash (rather than a
     * plain digest) ensures the stored hashes cannot be brute-forced from the
     * database alone.
     * </p>
     *
     * @param rawToken the plaintext token to hash
     * @param secret   the Base64-encoded secret key used to key the HMAC
     * @return the Base64-encoded HMAC-SHA256 hash of the token
     */
    public String hashToken(String rawToken, String secret) {
        try {
            byte[] secretBytes = Base64.getDecoder().decode(secret);

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretBytes, "HmacSHA256");
            mac.init(secretKeySpec);

            byte[] hashedBytes = mac.doFinal(rawToken.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalArgumentException e) {
            throw new RuntimeException("Failed to calculate HMAC-SHA256", e);
        }
    }

}

package live.craftmeet.authentication.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.crypto.SecretKey;
import java.util.Base64;

/**
 * Configuration properties record for JWT-related settings bound from the
 * {@code jwt} prefix.
 * This immutable configuration object is used by authentication components to
 * obtain the configured JWT values. When a Base64-encoded secret is supplied,
 * the record's construction logic decodes it and derives an HMAC secret key
 * suitable for signing and verifying JWTs; when no secret is supplied, no
 * derived key is created.
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtConfig(
        String issuer,
        String secret,
        Long refreshTokenExpiration,
        Long accessTokenExpiration,
        SecretKey secretKey) {
    public JwtConfig {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalArgumentException("jwt.secret must be provided in application properties");
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(secret);
            secretKey = Keys.hmacShaKeyFor(decoded);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("invalid base64-encoded secret for JWT", e);
        } catch (io.jsonwebtoken.security.WeakKeyException e) {
            throw new IllegalArgumentException("weak secret key for JWT", e);
        }
    }
}

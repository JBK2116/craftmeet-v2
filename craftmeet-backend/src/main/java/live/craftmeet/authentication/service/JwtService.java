package live.craftmeet.authentication.service;

import io.jsonwebtoken.*;
import live.craftmeet.authentication.jwt.InvalidJwtException;
import live.craftmeet.authentication.jwt.JwtConfig;
import live.craftmeet.authentication.jwt.JwtType;
import live.craftmeet.user.repository.UserRepository;

import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class JwtService {
    private final Environment environment;
    private final JwtConfig jwtProperties;
    private final UserRepository userRepository;

    public JwtService(Environment environment, JwtConfig jwtProperties, UserRepository userRepository) {
        this.environment = environment;
        this.jwtProperties = jwtProperties;
        this.userRepository = userRepository;
    }

    /**
     * Generates a signed compact JSON Web Token for the specified user and token
     * type.
     * The token includes the configured issuer, the user identifier as the subject,
     * the issued-at time, an expiration time determined by the token type, and a
     * type claim.
     * If a token identifier is supplied, it is included as the jti claim.
     *
     * @param userId  the identifier of the user for whom the token is generated
     * @param jwtType the type of token to generate, which determines the expiration
     *                duration and type claim
     * @param jti     the unique token identifier to include as the jti claim, or
     *                null to omit it
     * @return the generated token as a compact JSON Web Token string
     */
    public String generate(UUID userId, JwtType jwtType, UUID jti) {
        Instant now = Instant.now();

        Date expiration;
        if (jwtType == JwtType.ACCESS) {
            expiration = Date.from(now.plusMillis(jwtProperties.accessTokenExpiration()));
        } else {
            expiration = Date.from(now.plusMillis(jwtProperties.refreshTokenExpiration()));
        }

        JwtBuilder builder = Jwts.builder().issuer(jwtProperties.issuer()).subject(userId.toString())
                .issuedAt(Date.from(now)).expiration(expiration).claim("type", jwtType.getType());
        if (jti != null) {
            builder.id(jti.toString());
        }

        return builder.signWith(jwtProperties.secretKey()).compact();
    }

    /**
     * Validates the supplied compact JSON Web Token against the configured signing
     * key and expected token type.
     * The token must contain a matching type claim, the configured issuer, and a
     * subject that identifies an existing user.
     * For refresh tokens, the token must also contain a jti claim that corresponds
     * to a tracked refresh token.
     *
     * @param token   the compact JSON Web Token string to validate
     * @param jwtType the expected token type that the token claims must match
     * @return the validated claims extracted from the token
     * @throws InvalidJwtException if the token is malformed, has an invalid
     *                             signature, is expired, or fails any required
     *                             claim, user, or refresh token validation
     */
    public Claims validate(String token, JwtType jwtType) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(jwtProperties.secretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String typeStr = claims.get("type", String.class);
            if (typeStr == null || !JwtType.fromString(typeStr).equals(jwtType)) {
                throw new InvalidJwtException(jwtType);
            }

            String issuer = claims.getIssuer();
            if (issuer == null || !issuer.equals(jwtProperties.issuer())) {
                throw new InvalidJwtException(jwtType);
            }

            String subject = claims.getSubject();
            if (subject == null) {
                throw new InvalidJwtException(jwtType);
            }
            UUID userId = UUID.fromString(subject);
            if (!userRepository.existsById(userId)) {
                throw new InvalidJwtException(jwtType);
            }

            if (jwtType == JwtType.REFRESH) {
                String jti = claims.getId();
                if (jti == null) {
                    throw new InvalidJwtException(jwtType);
                }
            }

            return claims;
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidJwtException(jwtType);
        }
    }

    /**
     * Builds an HTTP cookie carrying the given signed JWT.
     * The cookie name and lifetime depend on the token type: access tokens use
     * the configured access-token expiration, while refresh tokens use the
     * refresh-token expiration. The resulting cookie is HTTP-only, secure,
     * SameSite=Strict, and scoped to the root path.
     *
     * @param jwt     the signed compact JWT value to store in the cookie
     * @param jwtType the type of token, determining the cookie name and lifetime
     * @return the constructed response cookie
     */
    public ResponseCookie buildToken(String jwt, JwtType jwtType) {
        if (jwtType.getType().equals(JwtType.ACCESS.getType())) {
            return ResponseCookie.from(JwtType.ACCESS.getType(), jwt)
                    .httpOnly(true)
                    .secure(!isDev())
                    .path("/")
                    .maxAge(TimeUnit.MILLISECONDS.toSeconds(jwtProperties.accessTokenExpiration()))
                    .sameSite(SameSite.STRICT.toString())
                    .build();
        } else {
            return ResponseCookie.from(JwtType.REFRESH.getType(), jwt)
                    .httpOnly(true)
                    .secure(!isDev())
                    .path("/")
                    .maxAge(TimeUnit.MILLISECONDS.toSeconds(jwtProperties.refreshTokenExpiration()))
                    .sameSite(SameSite.STRICT.toString())
                    .build();
        }

    }

    /**
     * Builds an HTTP cookie that clears the token of the given type on logout.
     * The cookie carries an empty value and an immediate expiration (a max age of
     * zero) so the browser discards it, while matching the name, path, and
     * attributes of the corresponding token cookie. The resulting cookie is
     * HTTP-only, secure, SameSite=Strict, and scoped to the root path.
     *
     * @param jwtType the type of token whose cookie should be cleared
     * @return the constructed logout cookie
     */
    public ResponseCookie buildLogoutToken(JwtType jwtType) {
        if (jwtType.getType().equals(JwtType.ACCESS.getType())) {
            return ResponseCookie.from(JwtType.ACCESS.getType(), "")
                    .httpOnly(true)
                    .secure(!isDev())
                    .path("/")
                    .maxAge(0)
                    .sameSite(SameSite.STRICT.toString())
                    .build();
        } else {
            return ResponseCookie.from(JwtType.REFRESH.getType(), "")
                    .httpOnly(true)
                    .secure(!isDev())
                    .path("/")
                    .maxAge(0)
                    .sameSite(SameSite.STRICT.toString())
                    .build();
        }
    }

    /**
     * Determines whether the application is running under the development profile.
     *
     * @return {@code true} when {@code dev} is among the active Spring profiles
     */
    private boolean isDev() {
        return Arrays.asList(environment.getActiveProfiles()).contains("dev");
    }
}

package live.craftmeet.authentication.service;

import io.jsonwebtoken.*;
import live.craftmeet.authentication.config.JwtConfig;
import live.craftmeet.authentication.config.JwtType;
import live.craftmeet.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final JwtConfig jwtProperties;
    private final UserRepository userRepository;

    public JwtService(JwtConfig jwtProperties, UserRepository userRepository) {
        this.jwtProperties = jwtProperties;
        this.userRepository = userRepository;
    }


    /**
     * Generates a signed compact JSON Web Token for the specified user and token type.
     * The token includes the configured issuer, the user identifier as the subject,
     * the issued-at time, an expiration time determined by the token type, and a type claim.
     * If a token identifier is supplied, it is included as the jti claim.
     *
     * @param userId  the identifier of the user for whom the token is generated
     * @param jwtType the type of token to generate, which determines the expiration duration and type claim
     * @param jti     the unique token identifier to include as the jti claim, or null to omit it
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

        JwtBuilder builder = Jwts.builder().
                            issuer(jwtProperties.issuer()).
                            subject(userId.toString()).
                            issuedAt(Date.from(now)).
                            expiration(expiration).
                            claim("type", jwtType.getType());
        if (jti != null) {
            builder.id(jti.toString());
        }

        return builder.signWith(jwtProperties.secretKey()).compact();
    }

    /**
     * Validates the supplied compact JSON Web Token against the configured signing key and expected token type.
     * The token must contain a matching type claim, the configured issuer, and a subject that identifies an existing user.
     * For refresh tokens, the token must also contain a jti claim that corresponds to a tracked refresh token.
     *
     * @param token the compact JSON Web Token string to validate
     * @param jwtType the expected token type that the token claims must match
     * @return the validated claims extracted from the token
     * @throws JwtException if the token is malformed, has an invalid signature, is expired, or fails any required claim, user, or refresh token validation
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
                throw new JwtException("invalid jwt token: type missing from claims or invalid");
            }

            String issuer = claims.getIssuer();
            if (issuer == null || !issuer.equals(jwtProperties.issuer())) {
                throw new JwtException("invalid jwt token: issuer missing from claims or invalid");
            }

            String subject = claims.getSubject();
            if (subject == null) {
                throw new JwtException("invalid jwt token: subject missing from claims");
            }
            UUID userId = UUID.fromString(subject);
            if (!userRepository.existsById(userId)) {
                throw new JwtException("invalid jwt token: no existing user found");
            }

            if (jwtType == JwtType.REFRESH) {
                String jti = claims.getId();
                if (jti == null) {
                    throw new JwtException("invalid jwt token: token missing jti");
                }
            }

            return claims;
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("invalid jwt token: " + e.getMessage());
        }
    }
}

package live.craftmeet.authentication.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import live.craftmeet.authentication.jwt.InvalidJwtException;
import live.craftmeet.authentication.jwt.JwtConfig;
import live.craftmeet.authentication.jwt.JwtType;
import live.craftmeet.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String ISSUER = "craftmeet";
    private static final String SECRET = Base64.getEncoder().encodeToString(new byte[32]);

    @Mock
    private UserRepository userRepository;

    private JwtConfig jwtConfig;
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtConfig = new JwtConfig(ISSUER, SECRET, 60_000L, 900_000L, null);
        jwtService = new JwtService(jwtConfig, userRepository);
    }

    @Test
    void generate_createsSignedAccessTokenWithExpectedClaims() {
        String token = jwtService.generate(USER_ID, JwtType.ACCESS, null);

        Claims claims = Jwts.parser()
                .verifyWith(jwtConfig.secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getIssuer()).isEqualTo(ISSUER);
        assertThat(claims.getSubject()).isEqualTo(USER_ID.toString());
        assertThat(claims.get("type", String.class)).isEqualTo(JwtType.ACCESS.getType());
        assertThat(claims.getId()).isNull();
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }

    @Test
    void generate_includesJtiWhenProvided() {
        UUID jti = UUID.randomUUID();

        String token = jwtService.generate(USER_ID, JwtType.REFRESH, jti);

        Claims claims = Jwts.parser()
                .verifyWith(jwtConfig.secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getId()).isEqualTo(jti.toString());
        assertThat(claims.get("type", String.class)).isEqualTo(JwtType.REFRESH.getType());
    }

    @Test
    void validate_returnsClaimsForValidTokenAndExpectedType() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        String token = jwtService.generate(USER_ID, JwtType.ACCESS, null);

        Claims claims = jwtService.validate(token, JwtType.ACCESS);

        assertThat(claims.getSubject()).isEqualTo(USER_ID.toString());
    }

    @Test
    void validate_rejectsWrongExpectedType() {
        String token = jwtService.generate(USER_ID, JwtType.ACCESS, null);

        assertThatThrownBy(() -> jwtService.validate(token, JwtType.REFRESH))
                .isInstanceOf(InvalidJwtException.class)
                .hasMessage("Invalid refresh_token token");
    }

    @Test
    void validate_rejectsTokenForUnknownUser() {
        UUID unknownUserId = UUID.randomUUID();
        when(userRepository.existsById(unknownUserId)).thenReturn(false);
        String token = jwtService.generate(unknownUserId, JwtType.ACCESS, null);

        assertThatThrownBy(() -> jwtService.validate(token, JwtType.ACCESS))
                .isInstanceOf(InvalidJwtException.class)
                .hasMessage("Invalid access_token token");
    }

    @Test
    void validate_rejectsMalformedToken() {
        assertThatThrownBy(() -> jwtService.validate("not-a-jwt", JwtType.ACCESS))
                .isInstanceOf(InvalidJwtException.class)
                .hasMessage("Invalid access_token token");
    }
}

package live.craftmeet.authentication.config;

import io.jsonwebtoken.security.WeakKeyException;
import live.craftmeet.authentication.jwt.JwtConfig;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtConfigTest {

    private static final String VALID_SECRET = Base64.getEncoder().encodeToString(new byte[32]);

    @Test
    void createsSigningKeyFromValidBase64Secret() {
        JwtConfig config = new JwtConfig("craftmeet", VALID_SECRET, 60_000L, 900_000L, null);

        assertThat(config.secretKey()).isNotNull();
    }

    @Test
    void rejectsMissingSecret() {
        assertThatThrownBy(() -> new JwtConfig("craftmeet", null, 60_000L, 900_000L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("jwt.secret must be provided in application properties");
    }

    @Test
    void rejectsWeakSecret() {
        String weakSecret = Base64.getEncoder().encodeToString(new byte[16]);

        assertThatThrownBy(() -> new JwtConfig("craftmeet", weakSecret, 60_000L, 900_000L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasCauseInstanceOf(WeakKeyException.class);
    }
}

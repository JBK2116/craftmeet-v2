package live.craftmeet.authentication.config;

import live.craftmeet.authentication.jwt.JwtType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTypeTest {

    @Test
    void fromString_resolvesTokenTypeIgnoringCase() {
        assertThat(JwtType.fromString("ACCESS_TOKEN")).isEqualTo(JwtType.ACCESS);
        assertThat(JwtType.fromString("refresh_token")).isEqualTo(JwtType.REFRESH);
    }

    @Test
    void fromString_rejectsNullAndUnknownType() {
        assertThatThrownBy(() -> JwtType.fromString(null))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> JwtType.fromString("unknown"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

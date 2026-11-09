package live.craftmeet.user.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import live.craftmeet.crypto.HashingConfig;

class HashingConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(HashingConfig.class);

    @Test
    void testPasswordEncoderBeanExists() {
        contextRunner.run(context -> {
            PasswordEncoder passwordEncoder = context.getBean(PasswordEncoder.class);
            assert passwordEncoder != null;
        });
    }

    @Test
    void testPasswordEncoderHashingStrength() {
        contextRunner.run(context -> {
            PasswordEncoder passwordEncoder = context.getBean(PasswordEncoder.class);

            assert passwordEncoder != null;
            assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);

            int strength = (int) ReflectionTestUtils.getField(passwordEncoder, "strength");
            assertThat(strength).isEqualTo(12);
        });
    }

}

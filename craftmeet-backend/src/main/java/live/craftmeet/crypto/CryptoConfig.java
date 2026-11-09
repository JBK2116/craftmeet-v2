package live.craftmeet.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class CryptoConfig {

    @Value("${verify.token.secret}")
    private String verifyTokenSecret;

    @Value("${verify.token.expiration}")
    private long verifyTokenExpiration;

    @Value("${verify.token.cooldown}")
    private long verifyTokenCooldown;

    private static final int BCRYPT_STRENGTH = 12;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }

    @Bean
    public String verifyEmailTokenSecret() {
        return verifyTokenSecret;
    }

    @Bean
    public long verifyEmailTokenExpiration() {
        return verifyTokenExpiration;
    }

    @Bean
    public long verifyEmailTokenCooldown() {
        return verifyTokenCooldown;
    }

}

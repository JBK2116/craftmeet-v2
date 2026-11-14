package live.craftmeet.authentication.oauth;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties record for Google OAuth settings bound from the
 * {@code google} prefix.
 * This immutable configuration object supplies the client credentials and
 * redirect URI used by the Google OAuth authorization-code flow.
 */
@ConfigurationProperties(prefix = "google")
public record GoogleOAuthProperties(String clientId, String clientSecret, String redirectUri) {
}

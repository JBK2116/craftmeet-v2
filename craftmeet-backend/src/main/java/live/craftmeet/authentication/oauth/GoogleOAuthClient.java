package live.craftmeet.authentication.oauth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * HTTP client for Google's OAuth 2.0 / OpenID Connect endpoints.
 * Builds the authorization URL that starts the authorization-code flow, then
 * exchanges a returned authorization code for the account's Google subject and
 * email via Google's token and userinfo endpoints.
 */
@Component
public class GoogleOAuthClient {

    private static final String AUTHORIZATION_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String USERINFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";

    private final GoogleOAuthProperties properties;
    private final RestClient restClient;

    public GoogleOAuthClient(GoogleOAuthProperties properties, RestClient.Builder builder) {
        this.properties = properties;
        this.restClient = builder.build();
    }

    /**
     * Builds the Google authorization URL that begins the OAuth flow.
     *
     * @param state the random state value used to protect against CSRF
     * @return the fully-qualified authorization URL
     */
    public String buildAuthorizationUrl(String state) {
        return UriComponentsBuilder.fromUriString(AUTHORIZATION_URL)
                .queryParam("client_id", properties.clientId())
                .queryParam("redirect_uri", properties.redirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", "openid email profile")
                .queryParam("state", state)
                .toUriString();
    }

    /**
     * Exchanges an authorization code for the authenticated Google account's
     * subject and email address.
     *
     * @param code the authorization code returned by Google
     * @return the Google account's subject and email
     */
    public GoogleUserInfo exchangeCodeForUserInfo(String code) {
        TokenResponse tokenResponse = restClient.post()
                .uri(TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(tokenRequestBody(code))
                .retrieve()
                .body(TokenResponse.class);

        return restClient.get()
                .uri(USERINFO_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.access_token())
                .retrieve()
                .body(GoogleUserInfo.class);
    }

    private String tokenRequestBody(String code) {
        return UriComponentsBuilder.newInstance()
                .queryParam("grant_type", "authorization_code")
                .queryParam("code", code)
                .queryParam("client_id", properties.clientId())
                .queryParam("client_secret", properties.clientSecret())
                .queryParam("redirect_uri", properties.redirectUri())
                .encode()
                .build()
                .getQuery();
    }

    private record TokenResponse(String access_token) {
    }
}

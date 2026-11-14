package live.craftmeet.authentication.oauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class GoogleOAuthClientTest {

    private final GoogleOAuthProperties properties =
            new GoogleOAuthProperties("client-id", "client-secret", "http://localhost:8080/google/callback");
    private final RestClient.Builder builder = RestClient.builder();
    private final MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
    private final GoogleOAuthClient client = new GoogleOAuthClient(properties, builder);

    @Test
    void buildAuthorizationUrl_containsOAuthParams() {
        String url = client.buildAuthorizationUrl("state-123");
        assertThat(url)
                .startsWith("https://accounts.google.com/o/oauth2/v2/auth?")
                .contains("client_id=client-id")
                .contains("redirect_uri=http://localhost:8080/google/callback")
                .contains("response_type=code")
                .contains("state=state-123");
        // scope is "openid email profile"; UriComponentsBuilder encodes the spaces
        assertThat(url).contains("scope=openid%20email%20profile");
    }

    @Test
    void exchangeCodeForUserInfo_returnsSubAndEmail() {
        server.expect(requestTo("https://oauth2.googleapis.com/token"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"access_token\":\"tok-123\"}", MediaType.APPLICATION_JSON));
        server.expect(requestTo("https://www.googleapis.com/oauth2/v3/userinfo"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer tok-123"))
                .andRespond(withSuccess("{\"sub\":\"google-123\",\"email\":\"user@example.com\",\"name\":\"Jane\"}",
                        MediaType.APPLICATION_JSON));

        GoogleUserInfo info = client.exchangeCodeForUserInfo("auth-code");

        assertThat(info.sub()).isEqualTo("google-123");
        assertThat(info.email()).isEqualTo("user@example.com");
        server.verify();
    }
}

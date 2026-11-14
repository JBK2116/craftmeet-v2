package live.craftmeet.authentication.oauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import jakarta.servlet.http.Cookie;
import live.craftmeet.user.model.User;
import live.craftmeet.user.repository.UserRepository;

/**
 * End-to-end tests for the Google OAuth flow: {@code GET /google} redirects to
 * Google with a CSRF state cookie, and {@code GET /google/callback} provisions
 * a user and sets token cookies against a throwaway Testcontainers PostgreSQL,
 * with the Google HTTP client mocked.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@Transactional
class GoogleOAuthE2ETest {

    private static final String EMAIL = "oauthuser@example.com";
    private static final String GOOGLE_ID = "google-123";

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private GoogleOAuthClient googleOAuthClient;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Test
    void google_redirectsToGoogleWithStateCookie() throws Exception {
        when(googleOAuthClient.buildAuthorizationUrl(anyString()))
                .thenReturn("https://accounts.google.com/o/oauth2/v2/auth?client_id=x&state=abc");

        MvcResult result = mockMvc.perform(get("/google"))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION,
                        "https://accounts.google.com/o/oauth2/v2/auth?client_id=x&state=abc"))
                .andReturn();

        assertThat(result.getResponse().getCookie("oauth_state")).isNotNull();
        assertThat(result.getResponse().getCookie("oauth_state").getValue()).isNotBlank();
    }

    @Test
    void googleCallback_createsUserAndSetsTokenCookies() throws Exception {
        when(googleOAuthClient.buildAuthorizationUrl(anyString())).thenReturn("https://accounts.google.com/oauth");
        String state = stateAfterGoogleRedirect();
        when(googleOAuthClient.exchangeCodeForUserInfo("test-code")).thenReturn(new GoogleUserInfo(GOOGLE_ID, EMAIL));

        MvcResult result = mockMvc.perform(get("/google/callback")
                .param("code", "test-code")
                .param("state", state)
                .cookie(new Cookie("oauth_state", state)))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, frontendUrl))
                .andReturn();

        List<String> cookies = result.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
        assertThat(cookies).anyMatch(c -> c.startsWith("access_token="));
        assertThat(cookies).anyMatch(c -> c.startsWith("refresh_token="));

        User user = userRepository.findByEmail(EMAIL).orElseThrow();
        assertThat(user.getGoogleId()).isEqualTo(GOOGLE_ID);
        assertThat(user.isVerified()).isTrue();
    }

    @Test
    void googleCallback_redirectsToErrorOnInvalidState() throws Exception {
        mockMvc.perform(get("/google/callback")
                .param("code", "test-code")
                .param("state", "wrong")
                .cookie(new Cookie("oauth_state", "actual")))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, frontendUrl + "/login?error=oauth_failed"));
    }

    @Test
    void googleCallback_redirectsToErrorWhenTokenExchangeFails() throws Exception {
        when(googleOAuthClient.buildAuthorizationUrl(anyString())).thenReturn("https://accounts.google.com/oauth");
        String state = stateAfterGoogleRedirect();
        when(googleOAuthClient.exchangeCodeForUserInfo(anyString())).thenThrow(new RuntimeException("boom"));

        mockMvc.perform(get("/google/callback")
                .param("code", "test-code")
                .param("state", state)
                .cookie(new Cookie("oauth_state", state)))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, frontendUrl + "/login?error=oauth_failed"));
    }

    private String stateAfterGoogleRedirect() throws Exception {
        MvcResult result = mockMvc.perform(get("/google")).andReturn();
        return result.getResponse().getCookie("oauth_state").getValue();
    }
}

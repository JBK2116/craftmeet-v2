package live.craftmeet.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import live.craftmeet.authentication.dto.LoginRequest;
import live.craftmeet.authentication.repository.RefreshTokenRepository;
import live.craftmeet.crypto.CryptoService;
import live.craftmeet.user.model.User;
import live.craftmeet.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end tests for the logout flow: a POST to {@code /logout} with a
 * refresh-token cookie revokes the user's refresh tokens and clears both cookies
 * against a throwaway Testcontainers PostgreSQL.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@Transactional
class LogoutE2ETest {

    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "TestPassword123!";
    private static final String EMAIL = "testuser@example.com";

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private CryptoService cryptoService;

    @Test
    void logout_revokesTokensAndReturnsClearingCookies() throws Exception {
        User user = createUser(EMAIL, PASSWORD, true);
        String refreshToken = loginAndGetRefreshToken(EMAIL, PASSWORD);

        MvcResult result = mockMvc.perform(post("/logout")
                .cookie(new Cookie("refresh_token", refreshToken)))
                .andExpect(status().isOk())
                .andReturn();

        List<String> cookies = result.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
        assertThat(cookies).hasSize(2);

        String accessCookie = cookies.stream()
                .filter(c -> c.startsWith("access_token="))
                .findFirst()
                .orElseThrow();
        String refreshCookie = cookies.stream()
                .filter(c -> c.startsWith("refresh_token="))
                .findFirst()
                .orElseThrow();
        assertThat(cookieValue(accessCookie)).isEmpty();
        assertThat(cookieValue(refreshCookie)).isEmpty();
        assertThat(accessCookie).contains("Max-Age=0");
        assertThat(refreshCookie).contains("Max-Age=0");

        assertThat(refreshTokenRepository.findByUserId(user.getId())).isEmpty();
    }

    @Test
    void logout_withInvalidTokenStillReturnsClearingCookies() throws Exception {
        MvcResult result = mockMvc.perform(post("/logout")
                .cookie(new Cookie("refresh_token", "invalid-token")))
                .andExpect(status().isOk())
                .andReturn();

        List<String> cookies = result.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
        assertThat(cookies).hasSize(2);
        assertThat(cookies).anyMatch(c -> c.startsWith("access_token="));
        assertThat(cookies).anyMatch(c -> c.startsWith("refresh_token="));
    }

    @Test
    void logout_revokesTokenSoItCannotRefresh() throws Exception {
        createUser(EMAIL, PASSWORD, true);
        String refreshToken = loginAndGetRefreshToken(EMAIL, PASSWORD);

        mockMvc.perform(post("/logout")
                .cookie(new Cookie("refresh_token", refreshToken)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/refresh")
                .cookie(new Cookie("refresh_token", refreshToken)))
                .andExpect(status().isUnauthorized());
    }

    private User createUser(String email, String password, boolean verified) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(USERNAME);
        user.setPassword(cryptoService.hashPassword(password));
        user.setVerified(verified);
        if (verified) {
            user.setVerifiedAt(Instant.now());
        }
        return userRepository.save(user);
    }

    private String loginAndGetRefreshToken(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson(email, password)))
                .andExpect(status().isOk())
                .andReturn();

        String refreshCookie = result.getResponse().getHeaders(HttpHeaders.SET_COOKIE).stream()
                .filter(c -> c.startsWith("refresh_token="))
                .findFirst()
                .orElseThrow();
        return cookieValue(refreshCookie);
    }

    private String cookieValue(String setCookieHeader) {
        String value = setCookieHeader.substring(setCookieHeader.indexOf('=') + 1);
        int end = value.indexOf(';');
        return end == -1 ? value : value.substring(0, end);
    }

    private String loginJson(String email, String password) throws Exception {
        return objectMapper.writeValueAsString(new LoginRequest(email, password));
    }
}

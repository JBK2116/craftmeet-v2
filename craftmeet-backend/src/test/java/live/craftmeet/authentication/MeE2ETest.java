package live.craftmeet.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import live.craftmeet.authentication.dto.LoginRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end tests for the current-user flow: a GET to {@code /me} with an
 * access-token cookie returns the authenticated user's account information
 * against a throwaway Testcontainers PostgreSQL.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@Transactional
class MeE2ETest {

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
    private CryptoService cryptoService;

    @Test
    void me_returnsUserForValidAccessToken() throws Exception {
        User user = createUser(EMAIL, PASSWORD, true);
        String accessToken = loginAndGetAccessToken(EMAIL, PASSWORD);

        mockMvc.perform(get("/me")
                .cookie(new Cookie("access_token", accessToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.username").value(USERNAME))
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.verified").value(true))
                .andExpect(jsonPath("$.verified_at").exists());
    }

    @Test
    void me_rejectsInvalidAccessToken() throws Exception {
        mockMvc.perform(get("/me")
                .cookie(new Cookie("access_token", "invalid-token")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void me_rejectsRefreshTokenInsteadOfAccessToken() throws Exception {
        createUser(EMAIL, PASSWORD, true);
        String refreshToken = loginAndGetRefreshToken(EMAIL, PASSWORD);

        mockMvc.perform(get("/me")
                .cookie(new Cookie("access_token", refreshToken)))
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

    private String loginAndGetAccessToken(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson(email, password)))
                .andExpect(status().isOk())
                .andReturn();

        return tokenCookie(result.getResponse().getHeaders(HttpHeaders.SET_COOKIE), "access_token");
    }

    private String loginAndGetRefreshToken(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson(email, password)))
                .andExpect(status().isOk())
                .andReturn();

        return tokenCookie(result.getResponse().getHeaders(HttpHeaders.SET_COOKIE), "refresh_token");
    }

    private String tokenCookie(List<String> setCookieHeaders, String name) {
        String cookie = setCookieHeaders.stream()
                .filter(c -> c.startsWith(name + "="))
                .findFirst()
                .orElseThrow();
        return cookieValue(cookie);
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

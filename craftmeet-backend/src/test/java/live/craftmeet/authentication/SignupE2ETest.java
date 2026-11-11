package live.craftmeet.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import live.craftmeet.authentication.dto.SignupRequest;
import live.craftmeet.authentication.model.VerifyEmailToken;
import live.craftmeet.authentication.repository.VerifyEmailTokenRepository;
import live.craftmeet.email.EmailService;
import live.craftmeet.user.model.User;
import live.craftmeet.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end tests for the signup process: an HTTP POST to {@code /signup} flows
 * through the controller, services, and repositories against a throwaway
 * Testcontainers PostgreSQL, with only the outbound email provider mocked.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@Transactional
class SignupE2ETest {

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
    private VerifyEmailTokenRepository verifyEmailTokenRepository;

    @MockitoBean
    private EmailService emailService;

    @Test
    void signup_persistsUserAndTokenAndSendsEmail() throws Exception {
        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signupJson(USERNAME, PASSWORD, EMAIL)))
                .andExpect(status().isOk());

        User user = userRepository.findByEmail(EMAIL).orElseThrow();
        assertThat(user.getUsername()).isEqualTo(USERNAME);
        assertThat(user.getPassword()).isNotEqualTo(PASSWORD);
        assertThat(user.isVerified()).isFalse();

        VerifyEmailToken token = verifyEmailTokenRepository.findByUserId(user.getId()).orElseThrow();
        assertThat(token.getTokenHash()).isNotBlank();

        verify(emailService).sendVerificationEmail(eq(EMAIL), anyString());
    }

    @Test
    void signup_returnsBadRequestForInvalidPayload() throws Exception {
        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signupJson("ab", PASSWORD, EMAIL)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void signup_returnsConflictForDuplicateEmail() throws Exception {
        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signupJson(USERNAME, PASSWORD, EMAIL)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signupJson("anotheruser", PASSWORD, EMAIL)))
                .andExpect(status().isConflict());
    }

    private String signupJson(String username, String password, String email) throws Exception {
        return objectMapper.writeValueAsString(new SignupRequest(username, password, email));
    }
}

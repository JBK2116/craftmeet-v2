package live.craftmeet.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import live.craftmeet.authentication.dto.SignupRequest;
import live.craftmeet.authentication.repository.VerifyEmailTokenRepository;
import live.craftmeet.crypto.CryptoService;
import live.craftmeet.email.EmailService;
import live.craftmeet.user.model.User;
import live.craftmeet.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end tests for resending the verification email: a POST to
 * {@code /verify} generates a fresh token and emails it against a throwaway
 * Testcontainers PostgreSQL, with only the outbound email provider mocked.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@Transactional
class SendVerificationEmailE2ETest {

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

    @Autowired
    private CryptoService cryptoService;

    @MockitoBean
    private EmailService emailService;

    @Test
    void sendVerificationEmail_createsTokenAndSendsEmail() throws Exception {
        User user = createUser(EMAIL, false);

        mockMvc.perform(post("/verify").param("email", EMAIL))
                .andExpect(status().isOk());

        assertThat(verifyEmailTokenRepository.findByUserId(user.getId())).isPresent();
        verify(emailService).sendVerificationEmail(eq(EMAIL), anyString());
    }

    @Test
    void sendVerificationEmail_rejectsUnknownEmail() throws Exception {
        mockMvc.perform(post("/verify").param("email", "unknown@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void sendVerificationEmail_rejectsVerifiedUser() throws Exception {
        createUser(EMAIL, true);

        mockMvc.perform(post("/verify").param("email", EMAIL))
                .andExpect(status().isConflict());
    }

    @Test
    void sendVerificationEmail_rejectsDuringCooldown() throws Exception {
        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signupJson()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/verify").param("email", EMAIL))
                .andExpect(status().isTooManyRequests());
    }

    private User createUser(String email, boolean verified) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(USERNAME);
        user.setPassword(cryptoService.hashPassword(PASSWORD));
        user.setVerified(verified);
        if (verified) {
            user.setVerifiedAt(Instant.now());
        }
        return userRepository.save(user);
    }

    private String signupJson() throws Exception {
        return objectMapper.writeValueAsString(new SignupRequest(USERNAME, PASSWORD, EMAIL));
    }
}

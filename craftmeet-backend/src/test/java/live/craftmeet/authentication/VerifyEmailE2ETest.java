package live.craftmeet.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import live.craftmeet.authentication.dto.SignupRequest;
import live.craftmeet.email.EmailService;
import live.craftmeet.user.model.User;
import live.craftmeet.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end tests for the email verification flow: a GET to {@code /verify}
 * marks the associated user as verified against a throwaway Testcontainers
 * PostgreSQL, with only the outbound email provider mocked.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@Transactional
class VerifyEmailE2ETest {

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

    @MockitoBean
    private EmailService emailService;

    @Test
    void verifyEmail_marksUserVerified() throws Exception {
        String token = signupAndCaptureToken();

        mockMvc.perform(get("/verify").param("token", token))
                .andExpect(status().isOk());

        User user = userRepository.findByEmail(EMAIL).orElseThrow();
        assertThat(user.isVerified()).isTrue();
        assertThat(user.getVerifiedAt()).isNotNull();
    }

    @Test
    void verifyEmail_rejectsUnknownToken() throws Exception {
        mockMvc.perform(get("/verify").param("token", "unknown-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void verifyEmail_rejectsAlreadyVerifiedUser() throws Exception {
        String token = signupAndCaptureToken();

        mockMvc.perform(get("/verify").param("token", token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/verify").param("token", token))
                .andExpect(status().isConflict());
    }

    private String signupAndCaptureToken() throws Exception {
        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SignupRequest(USERNAME, PASSWORD, EMAIL))))
                .andExpect(status().isOk());

        ArgumentCaptor<String> tokenCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService).sendVerificationEmail(eq(EMAIL), tokenCaptor.capture());
        return tokenCaptor.getValue();
    }
}

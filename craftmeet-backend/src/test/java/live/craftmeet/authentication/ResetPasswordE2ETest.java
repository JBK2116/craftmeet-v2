package live.craftmeet.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import live.craftmeet.authentication.dto.ResetPasswordRequest;
import live.craftmeet.authentication.dto.SendResetPasswordEmailRequest;
import live.craftmeet.authentication.repository.ResetPasswordTokenRepository;
import live.craftmeet.crypto.CryptoService;
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

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end tests for the reset-password flow: a POST to
 * {@code /reset-password} validates the reset token and updates the user's
 * password against a throwaway Testcontainers PostgreSQL, with only the outbound
 * email provider mocked.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@Transactional
class ResetPasswordE2ETest {

    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "TestPassword123!";
    private static final String NEW_PASSWORD = "NewTestPassword123!";
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
    private ResetPasswordTokenRepository resetPasswordTokenRepository;

    @Autowired
    private CryptoService cryptoService;

    @MockitoBean
    private EmailService emailService;

    @Test
    void resetPassword_updatesPasswordAndDeletesToken() throws Exception {
        User user = createVerifiedUser(EMAIL);
        String token = requestResetToken(EMAIL);

        mockMvc.perform(post("/reset-password")
                .param("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(resetPasswordJson(NEW_PASSWORD)))
                .andExpect(status().isOk());

        User updated = userRepository.findByEmail(EMAIL).orElseThrow();
        assertThat(cryptoService.checkPassword(NEW_PASSWORD, updated.getPassword())).isTrue();
        assertThat(resetPasswordTokenRepository.findByUserId(user.getId())).isEmpty();
    }

    @Test
    void resetPassword_rejectsInvalidToken() throws Exception {
        mockMvc.perform(post("/reset-password")
                .param("token", "invalid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(resetPasswordJson(NEW_PASSWORD)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void resetPassword_rejectsReusedToken() throws Exception {
        createVerifiedUser(EMAIL);
        String token = requestResetToken(EMAIL);

        mockMvc.perform(post("/reset-password")
                .param("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(resetPasswordJson(NEW_PASSWORD)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/reset-password")
                .param("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(resetPasswordJson(NEW_PASSWORD)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void resetPassword_rejectsInvalidPassword() throws Exception {
        mockMvc.perform(post("/reset-password")
                .param("token", "some-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(resetPasswordJson("")))
                .andExpect(status().isBadRequest());
    }

    private User createVerifiedUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(USERNAME);
        user.setPassword(cryptoService.hashPassword(PASSWORD));
        user.setVerified(true);
        user.setVerifiedAt(Instant.now());
        return userRepository.save(user);
    }

    private String requestResetToken(String email) throws Exception {
        mockMvc.perform(post("/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SendResetPasswordEmailRequest(email))))
                .andExpect(status().isOk());

        ArgumentCaptor<String> tokenCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService).sendResetPasswordEmail(eq(email), tokenCaptor.capture());
        return tokenCaptor.getValue();
    }

    private String resetPasswordJson(String newPassword) throws Exception {
        return objectMapper.writeValueAsString(new ResetPasswordRequest(newPassword));
    }
}

package live.craftmeet.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import live.craftmeet.crypto.CryptoService;
import live.craftmeet.user.dto.UpdateUsernameRequest;
import live.craftmeet.user.model.User;
import live.craftmeet.user.repository.UserRepository;
import live.craftmeet.user.utils.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end tests for the update-username flow: a PUT to {@code /users/{id}}
 * with the authenticated user's ID as a request attribute updates the user's
 * own username against a throwaway Testcontainers PostgreSQL.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@Transactional
class UpdateUsernameE2ETest {

    private static final String USERNAME = "olduser";
    private static final String NEW_USERNAME = "newuser";
    private static final String OTHER_USERNAME = "otheruser";
    private static final String PASSWORD = "TestPassword123!";
    private static final String EMAIL = "updateuser@example.com";
    private static final String OTHER_EMAIL = "otherupdateuser@example.com";

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
    void updateUsername_returnsUpdatedUser() throws Exception {
        User user = createUser(EMAIL, USERNAME, true);

        mockMvc.perform(put("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(usernameJson(NEW_USERNAME))
                .requestAttr(Constants.USER_ID_ATTRIBUTE, user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(NEW_USERNAME))
                .andExpect(jsonPath("$.email").value(EMAIL));

        assertThat(userRepository.findById(user.getId()).orElseThrow().getUsername()).isEqualTo(NEW_USERNAME);
    }

    @Test
    void updateUsername_rejectsAnotherUsersAccount() throws Exception {
        User user = createUser(EMAIL, USERNAME, true);
        User other = createUser(OTHER_EMAIL, OTHER_USERNAME, true);

        mockMvc.perform(put("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(usernameJson(NEW_USERNAME))
                .requestAttr(Constants.USER_ID_ATTRIBUTE, other.getId()))
                .andExpect(status().isForbidden());

        assertThat(userRepository.findById(user.getId()).orElseThrow().getUsername()).isEqualTo(USERNAME);
    }

    @Test
    void updateUsername_rejectsUnknownUser() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(put("/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(usernameJson(NEW_USERNAME))
                .requestAttr(Constants.USER_ID_ATTRIBUTE, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUsername_returnsBadRequestForInvalidUsername() throws Exception {
        User user = createUser(EMAIL, USERNAME, true);

        mockMvc.perform(put("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(usernameJson(""))
                .requestAttr(Constants.USER_ID_ATTRIBUTE, user.getId()))
                .andExpect(status().isBadRequest());
    }

    private User createUser(String email, String username, boolean verified) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(cryptoService.hashPassword(PASSWORD));
        user.setVerified(verified);
        if (verified) {
            user.setVerifiedAt(Instant.now());
        }
        return userRepository.save(user);
    }

    private String usernameJson(String username) throws Exception {
        return objectMapper.writeValueAsString(new UpdateUsernameRequest(username));
    }
}

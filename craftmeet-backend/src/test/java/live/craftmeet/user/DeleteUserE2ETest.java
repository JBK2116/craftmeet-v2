package live.craftmeet.user;

import live.craftmeet.crypto.CryptoService;
import live.craftmeet.user.model.User;
import live.craftmeet.user.repository.UserRepository;
import live.craftmeet.user.utils.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end tests for the delete-user flow: a DELETE to {@code /users/{id}} with
 * the authenticated user's ID as a request attribute removes the user's own
 * account against a throwaway Testcontainers PostgreSQL.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@Transactional
class DeleteUserE2ETest {

    private static final String USERNAME = "deleteuser";
    private static final String OTHER_USERNAME = "otherdeleteuser";
    private static final String PASSWORD = "TestPassword123!";
    private static final String EMAIL = "deleteuser@example.com";
    private static final String OTHER_EMAIL = "otherdeleteuser@example.com";

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CryptoService cryptoService;

    @Test
    void delete_returnsNoContentAndRemovesUser() throws Exception {
        User user = createUser(EMAIL, USERNAME, true);

        mockMvc.perform(delete("/users/" + user.getId())
                .requestAttr(Constants.USER_ID_ATTRIBUTE, user.getId()))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(user.getId())).isEmpty();
    }

    @Test
    void delete_rejectsAnotherUsersAccount() throws Exception {
        User user = createUser(EMAIL, USERNAME, true);
        User other = createUser(OTHER_EMAIL, OTHER_USERNAME, true);

        mockMvc.perform(delete("/users/" + user.getId())
                .requestAttr(Constants.USER_ID_ATTRIBUTE, other.getId()))
                .andExpect(status().isForbidden());

        assertThat(userRepository.findById(user.getId())).isPresent();
    }

    @Test
    void delete_rejectsUnknownUser() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(delete("/users/" + userId)
                .requestAttr(Constants.USER_ID_ATTRIBUTE, userId))
                .andExpect(status().isNotFound());
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
}

package live.craftmeet.meeting;

import com.fasterxml.jackson.databind.ObjectMapper;
import live.craftmeet.crypto.CryptoService;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end tests for the list-meetings flow: a GET to {@code /meetings} with
 * the authenticated user's ID as a request attribute returns every meeting owned
 * by that user, each with its questions and statistics, against a throwaway
 * Testcontainers PostgreSQL. Other users' meetings are excluded.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@Transactional
class GetMeetingsE2ETest {

    private static final String USERNAME = "meetinguser";
    private static final String OTHER_USERNAME = "othermeetinguser";
    private static final String PASSWORD = "TestPassword123!";
    private static final String EMAIL = "meetinguser@example.com";
    private static final String OTHER_EMAIL = "othermeetinguser@example.com";

    private static final String CREATE_JSON = """
            {
              "title": "Team Sync",
              "description": "Weekly sync meeting",
              "duration": 60,
              "participantCap": 20,
              "questions": [
                {
                  "type": "multiple_choice",
                  "prompt": "What is your favorite color?",
                  "position": 1,
                  "option1": "Red",
                  "option2": "Blue",
                  "allowMultiple": false
                },
                {
                  "type": "yes_no",
                  "prompt": "Did you enjoy the meeting?",
                  "position": 2
                }
              ]
            }
            """;

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
    void getMeetings_returnsOwnedMeetingsWithQuestionsAndStat() throws Exception {
        User user = createUser(EMAIL, USERNAME, PASSWORD, true);
        createMeeting(user.getId());
        createMeeting(user.getId());

        User other = createUser(OTHER_EMAIL, OTHER_USERNAME, PASSWORD, true);
        createMeeting(other.getId());

        mockMvc.perform(get("/meetings")
                .requestAttr(Constants.USER_ID_ATTRIBUTE, user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value(user.getId().toString()))
                .andExpect(jsonPath("$[0].questions.length()").value(2))
                .andExpect(jsonPath("$[0].statResponse.totalParticipants").value(0))
                .andExpect(jsonPath("$[1].userId").value(user.getId().toString()))
                .andExpect(jsonPath("$[1].questions.length()").value(2));
    }

    @Test
    void getMeetings_returnsEmptyListWhenUserHasNoMeetings() throws Exception {
        User user = createUser(EMAIL, USERNAME, PASSWORD, true);

        mockMvc.perform(get("/meetings")
                .requestAttr(Constants.USER_ID_ATTRIBUTE, user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    private UUID createMeeting(UUID userId) throws Exception {
        MvcResult result = mockMvc.perform(post("/meetings")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr(Constants.USER_ID_ATTRIBUTE, userId)
                .content(CREATE_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        return UUID.fromString(objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText());
    }

    private User createUser(String email, String username, String password, boolean verified) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(cryptoService.hashPassword(password));
        user.setVerified(verified);
        if (verified) {
            user.setVerifiedAt(Instant.now());
        }
        return userRepository.save(user);
    }
}

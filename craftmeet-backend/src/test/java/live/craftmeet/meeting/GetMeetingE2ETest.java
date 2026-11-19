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
 * End-to-end tests for the get-meeting flow: a GET to {@code /meetings/{id}}
 * with the authenticated user's ID as a request attribute returns an owned
 * meeting with its questions (ordered by position) and statistics against a
 * throwaway Testcontainers PostgreSQL.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@Transactional
class GetMeetingE2ETest {

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
    void get_returnsMeetingWithQuestionsAndStat() throws Exception {
        User user = createUser(EMAIL, USERNAME, PASSWORD, true);
        UUID meetingId = createMeeting(user.getId());

        mockMvc.perform(get("/meetings/" + meetingId)
                .requestAttr(Constants.USER_ID_ATTRIBUTE, user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(meetingId.toString()))
                .andExpect(jsonPath("$.title").value("Team Sync"))
                .andExpect(jsonPath("$.description").value("Weekly sync meeting"))
                .andExpect(jsonPath("$.duration").value(60))
                .andExpect(jsonPath("$.participantCap").value(20))
                .andExpect(jsonPath("$.questions.length()").value(2))
                .andExpect(jsonPath("$.questions[0].type").value("multiple_choice"))
                .andExpect(jsonPath("$.questions[0].position").value(1))
                .andExpect(jsonPath("$.questions[0].prompt").value("What is your favorite color?"))
                .andExpect(jsonPath("$.questions[1].type").value("yes_no"))
                .andExpect(jsonPath("$.questions[1].position").value(2))
                .andExpect(jsonPath("$.statResponse.totalParticipants").value(0))
                .andExpect(jsonPath("$.statResponse.totalQuestionsAsked").value(0));
    }

    @Test
    void get_rejectsUnknownMeeting() throws Exception {
        User user = createUser(EMAIL, USERNAME, PASSWORD, true);

        mockMvc.perform(get("/meetings/" + UUID.randomUUID())
                .requestAttr(Constants.USER_ID_ATTRIBUTE, user.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void get_rejectsMeetingOwnedByAnotherUser() throws Exception {
        User owner = createUser(EMAIL, USERNAME, PASSWORD, true);
        UUID meetingId = createMeeting(owner.getId());
        User other = createUser(OTHER_EMAIL, OTHER_USERNAME, PASSWORD, true);

        mockMvc.perform(get("/meetings/" + meetingId)
                .requestAttr(Constants.USER_ID_ATTRIBUTE, other.getId()))
                .andExpect(status().isNotFound());
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

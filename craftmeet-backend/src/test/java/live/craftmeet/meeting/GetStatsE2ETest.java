package live.craftmeet.meeting;

import com.fasterxml.jackson.databind.ObjectMapper;
import live.craftmeet.crypto.CryptoService;
import live.craftmeet.meeting.model.stat.Stat;
import live.craftmeet.meeting.repository.StatRepository;
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
 * End-to-end tests for the lifetime-stats flow: a GET to {@code /stats} with the
 * authenticated user's ID as a request attribute sums the statistics across that
 * user's meetings against a throwaway Testcontainers PostgreSQL, excluding other
 * users' meetings.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@Transactional
class GetStatsE2ETest {

    private static final String USERNAME = "statsuser";
    private static final String OTHER_USERNAME = "otherstatsuser";
    private static final String PASSWORD = "TestPassword123!";
    private static final String EMAIL = "statsuser@example.com";
    private static final String OTHER_EMAIL = "otherstatsuser@example.com";

    private static final String CREATE_JSON = """
            {
              "title": "Team Sync",
              "description": "Weekly sync meeting",
              "duration": 60,
              "participantCap": 20,
              "questions": [
                {"type": "yes_no", "prompt": "Enjoy?", "position": 1}
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
    private StatRepository statRepository;

    @Autowired
    private CryptoService cryptoService;

    @Test
    void getStats_returnsZerosWhenUserHasNoMeetings() throws Exception {
        User user = createUser(EMAIL, USERNAME, true);

        mockMvc.perform(get("/stats")
                .requestAttr(Constants.USER_ID_ATTRIBUTE, user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalParticipants").value(0))
                .andExpect(jsonPath("$.totalQuestionsAsked").value(0))
                .andExpect(jsonPath("$.totalResponsesReceived").value(0));
    }

    @Test
    void getStats_sumsOwnedMeetingsAndExcludesOthers() throws Exception {
        User user = createUser(EMAIL, USERNAME, true);
        UUID meeting1 = createMeeting(user.getId());
        UUID meeting2 = createMeeting(user.getId());
        setStat(meeting1, 10, 5, 40);
        setStat(meeting2, 7, 3, 25);

        User other = createUser(OTHER_EMAIL, OTHER_USERNAME, true);
        setStat(createMeeting(other.getId()), 100, 100, 100);

        mockMvc.perform(get("/stats")
                .requestAttr(Constants.USER_ID_ATTRIBUTE, user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalParticipants").value(17))
                .andExpect(jsonPath("$.totalQuestionsAsked").value(8))
                .andExpect(jsonPath("$.totalResponsesReceived").value(65));
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

    private void setStat(UUID meetingId, int participants, int questions, int responses) {
        Stat stat = statRepository.findByMeetingId(meetingId).orElseThrow();
        stat.setTotalParticipants(participants);
        stat.setTotalQuestionsAsked(questions);
        stat.setTotalResponsesReceived(responses);
        statRepository.save(stat);
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

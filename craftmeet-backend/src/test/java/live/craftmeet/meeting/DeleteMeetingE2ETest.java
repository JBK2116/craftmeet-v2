package live.craftmeet.meeting;

import com.fasterxml.jackson.databind.ObjectMapper;
import live.craftmeet.crypto.CryptoService;
import live.craftmeet.meeting.repository.MeetingRepository;
import live.craftmeet.meeting.repository.QuestionRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end tests for the delete-meeting flow: a DELETE to {@code /meetings/{id}}
 * with the authenticated user's ID as a request attribute removes an owned
 * meeting and its questions and statistics against a throwaway Testcontainers
 * PostgreSQL.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@Transactional
class DeleteMeetingE2ETest {

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
                {"type": "yes_no", "prompt": "Original question?", "position": 1}
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
    private MeetingRepository meetingRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private StatRepository statRepository;

    @Autowired
    private CryptoService cryptoService;

    @Test
    void delete_returnsNoContentAndRemovesMeetingWithQuestionsAndStat() throws Exception {
        User user = createUser(EMAIL, USERNAME, PASSWORD, true);
        UUID meetingId = createMeeting(user.getId());

        mockMvc.perform(delete("/meetings/" + meetingId)
                .requestAttr(Constants.USER_ID_ATTRIBUTE, user.getId()))
                .andExpect(status().isNoContent());

        assertThat(meetingRepository.findByUserId(user.getId())).isEmpty();
        assertThat(questionRepository.findByMeetingIdOrderByPositionAsc(meetingId)).isEmpty();
        assertThat(statRepository.findByMeetingId(meetingId)).isEmpty();
    }

    @Test
    void delete_rejectsUnknownMeeting() throws Exception {
        User user = createUser(EMAIL, USERNAME, PASSWORD, true);

        mockMvc.perform(delete("/meetings/" + UUID.randomUUID())
                .requestAttr(Constants.USER_ID_ATTRIBUTE, user.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_rejectsMeetingOwnedByAnotherUser() throws Exception {
        User owner = createUser(EMAIL, USERNAME, PASSWORD, true);
        UUID meetingId = createMeeting(owner.getId());
        User other = createUser(OTHER_EMAIL, OTHER_USERNAME, PASSWORD, true);

        mockMvc.perform(delete("/meetings/" + meetingId)
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

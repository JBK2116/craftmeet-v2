package live.craftmeet.meeting;

import com.fasterxml.jackson.databind.ObjectMapper;
import live.craftmeet.crypto.CryptoService;
import live.craftmeet.meeting.model.meeting.Meeting;
import live.craftmeet.meeting.model.question.Question;
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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end tests for the update-meeting flow: a PUT to {@code /meetings/{id}}
 * with the authenticated user's ID as a request attribute updates an owned,
 * not-yet-started meeting and replaces its questions against a throwaway
 * Testcontainers PostgreSQL.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@Transactional
class UpdateMeetingE2ETest {

    private static final String USERNAME = "meetinguser";
    private static final String OTHER_USERNAME = "othermeetinguser";
    private static final String PASSWORD = "TestPassword123!";
    private static final String EMAIL = "meetinguser@example.com";
    private static final String OTHER_EMAIL = "othermeetinguser@example.com";

    private static final String CREATE_JSON = """
            {
              "title": "Original Title",
              "description": "Original description",
              "duration": 30,
              "participantCap": 10,
              "questions": [
                {"type": "yes_no", "prompt": "Original question?", "position": 1}
              ]
            }
            """;

    private static final String UPDATE_JSON = """
            {
              "title": "Updated Title",
              "description": "Updated description",
              "duration": 60,
              "participantCap": 20,
              "questions": [
                {
                  "type": "multiple_choice",
                  "prompt": "Updated MC?",
                  "position": 1,
                  "option1": "A",
                  "option2": "B",
                  "allowMultiple": false
                },
                {
                  "type": "yes_no",
                  "prompt": "Updated YN?",
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
    private MeetingRepository meetingRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private StatRepository statRepository;

    @Autowired
    private CryptoService cryptoService;

    @Test
    void update_returnsUpdatedMeeting() throws Exception {
        User user = createUser(EMAIL, USERNAME, PASSWORD, true);
        UUID meetingId = createMeeting(user.getId());

        mockMvc.perform(put("/meetings/" + meetingId)
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr(Constants.USER_ID_ATTRIBUTE, user.getId())
                .content(UPDATE_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(meetingId.toString()))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.duration").value(60))
                .andExpect(jsonPath("$.participantCap").value(20))
                .andExpect(jsonPath("$.questions.length()").value(2))
                .andExpect(jsonPath("$.questions[0].type").value("multiple_choice"))
                .andExpect(jsonPath("$.questions[0].prompt").value("Updated MC?"))
                .andExpect(jsonPath("$.questions[1].type").value("yes_no"));
    }

    @Test
    void update_persistsChangesAndRotatesQuestions() throws Exception {
        User user = createUser(EMAIL, USERNAME, PASSWORD, true);
        UUID meetingId = createMeeting(user.getId());

        String originalRoomCode = meetingRepository.findById(meetingId).orElseThrow().getRoomCode();

        mockMvc.perform(put("/meetings/" + meetingId)
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr(Constants.USER_ID_ATTRIBUTE, user.getId())
                .content(UPDATE_JSON))
                .andExpect(status().isOk());

        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow();
        assertThat(meeting.getTitle()).isEqualTo("Updated Title");
        assertThat(meeting.getDescription()).isEqualTo("Updated description");
        assertThat(meeting.getSettings().getDuration()).isEqualTo(60);
        assertThat(meeting.getSettings().getParticipantCap()).isEqualTo(20);
        assertThat(meeting.getRoomCode()).isEqualTo(originalRoomCode);

        List<Question> questions = questionRepository.findByMeetingIdOrderByPositionAsc(meetingId);
        assertThat(questions).hasSize(2);
        assertThat(questions.get(0).getPrompt()).isEqualTo("Updated MC?");
        assertThat(questions.get(1).getPrompt()).isEqualTo("Updated YN?");

        assertThat(statRepository.findByMeetingId(meetingId)).isPresent();
    }

    @Test
    void update_rejectsUnknownMeeting() throws Exception {
        User user = createUser(EMAIL, USERNAME, PASSWORD, true);

        mockMvc.perform(put("/meetings/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr(Constants.USER_ID_ATTRIBUTE, user.getId())
                .content(UPDATE_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_rejectsMeetingOwnedByAnotherUser() throws Exception {
        User owner = createUser(EMAIL, USERNAME, PASSWORD, true);
        UUID meetingId = createMeeting(owner.getId());
        User other = createUser(OTHER_EMAIL, OTHER_USERNAME, PASSWORD, true);

        mockMvc.perform(put("/meetings/" + meetingId)
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr(Constants.USER_ID_ATTRIBUTE, other.getId())
                .content(UPDATE_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_rejectsStartedMeeting() throws Exception {
        User user = createUser(EMAIL, USERNAME, PASSWORD, true);
        UUID meetingId = createMeeting(user.getId());

        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow();
        meeting.getSession().setStartTime(Instant.now());

        mockMvc.perform(put("/meetings/" + meetingId)
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr(Constants.USER_ID_ATTRIBUTE, user.getId())
                .content(UPDATE_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void update_rejectsInvalidPayload() throws Exception {
        User user = createUser(EMAIL, USERNAME, PASSWORD, true);
        UUID meetingId = createMeeting(user.getId());

        mockMvc.perform(put("/meetings/" + meetingId)
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr(Constants.USER_ID_ATTRIBUTE, user.getId())
                .content("""
                        {
                          "title": "",
                          "duration": 60,
                          "participantCap": 20,
                          "questions": [
                            {"type": "yes_no", "prompt": "Question?", "position": 1}
                          ]
                        }
                        """))
                .andExpect(status().isBadRequest());
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

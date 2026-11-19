package live.craftmeet.meeting;

import com.fasterxml.jackson.databind.ObjectMapper;
import live.craftmeet.crypto.CryptoService;
import live.craftmeet.meeting.model.meeting.Meeting;
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
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end tests for the create-meeting flow: a POST to {@code /meetings}
 * with the authenticated user's ID as a request attribute persists the meeting,
 * its questions, and a statistics record against a throwaway Testcontainers
 * PostgreSQL.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@Transactional
class CreateMeetingE2ETest {

    private static final String USERNAME = "meetinguser";
    private static final String PASSWORD = "TestPassword123!";
    private static final String EMAIL = "meetinguser@example.com";

    private static final String MEETING_JSON = """
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
                  "option3": "Green",
                  "allowMultiple": true
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
    private MeetingRepository meetingRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private StatRepository statRepository;

    @Autowired
    private CryptoService cryptoService;

    @Test
    void create_returnsCreatedMeetingWithQuestionsAndStat() throws Exception {
        User user = createUser(EMAIL, PASSWORD, true);

        mockMvc.perform(post("/meetings")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr(Constants.USER_ID_ATTRIBUTE, user.getId())
                .content(MEETING_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.userId").value(user.getId().toString()))
                .andExpect(jsonPath("$.title").value("Team Sync"))
                .andExpect(jsonPath("$.description").value("Weekly sync meeting"))
                .andExpect(jsonPath("$.roomCode").value(matchesPattern("\\d{8}")))
                .andExpect(jsonPath("$.duration").value(60))
                .andExpect(jsonPath("$.participantCap").value(20))
                .andExpect(jsonPath("$.startTime").value(nullValue()))
                .andExpect(jsonPath("$.endTime").value(nullValue()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty())
                .andExpect(jsonPath("$.questions.length()").value(2))
                .andExpect(jsonPath("$.questions[0].type").value("multiple_choice"))
                .andExpect(jsonPath("$.questions[0].prompt").value("What is your favorite color?"))
                .andExpect(jsonPath("$.questions[0].option1").value("Red"))
                .andExpect(jsonPath("$.questions[0].allowMultiple").value(true))
                .andExpect(jsonPath("$.questions[1].type").value("yes_no"))
                .andExpect(jsonPath("$.statResponse.totalParticipants").value(0))
                .andExpect(jsonPath("$.statResponse.totalQuestionsAsked").value(0))
                .andExpect(jsonPath("$.statResponse.totalResponsesReceived").value(0))
                .andExpect(jsonPath("$.statResponse.durationSeconds").value(nullValue()));
    }

    @Test
    void create_persistsMeetingQuestionsAndStat() throws Exception {
        User user = createUser(EMAIL, PASSWORD, true);

        MvcResult result = mockMvc.perform(post("/meetings")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr(Constants.USER_ID_ATTRIBUTE, user.getId())
                .content(MEETING_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        UUID meetingId = UUID.fromString(
                objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText());

        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow();
        assertThat(meeting.getUserId()).isEqualTo(user.getId());
        assertThat(meeting.getTitle()).isEqualTo("Team Sync");
        assertThat(meeting.getRoomCode()).hasSize(8);

        assertThat(questionRepository.findByMeetingIdOrderByPositionAsc(meetingId)).hasSize(2);
        assertThat(statRepository.findByMeetingId(meetingId)).isPresent();
    }

    @Test
    void create_rejectsBlankTitle() throws Exception {
        User user = createUser(EMAIL, PASSWORD, true);

        mockMvc.perform(post("/meetings")
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

    @Test
    void create_rejectsOutOfRangeDuration() throws Exception {
        User user = createUser(EMAIL, PASSWORD, true);

        mockMvc.perform(post("/meetings")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr(Constants.USER_ID_ATTRIBUTE, user.getId())
                .content("""
                        {
                          "title": "Team Sync",
                          "duration": 0,
                          "participantCap": 20,
                          "questions": [
                            {"type": "yes_no", "prompt": "Question?", "position": 1}
                          ]
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_rejectsEmptyQuestions() throws Exception {
        User user = createUser(EMAIL, PASSWORD, true);

        mockMvc.perform(post("/meetings")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr(Constants.USER_ID_ATTRIBUTE, user.getId())
                .content("""
                        {
                          "title": "Team Sync",
                          "duration": 60,
                          "participantCap": 20,
                          "questions": []
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    private User createUser(String email, String password, boolean verified) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(USERNAME);
        user.setPassword(cryptoService.hashPassword(password));
        user.setVerified(verified);
        if (verified) {
            user.setVerifiedAt(Instant.now());
        }
        return userRepository.save(user);
    }
}

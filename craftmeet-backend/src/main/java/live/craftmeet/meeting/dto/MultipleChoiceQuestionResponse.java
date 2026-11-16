package live.craftmeet.meeting.dto;

import java.util.UUID;

/**
 * A multiple-choice question returned to clients, with up to four options and
 * optional multi-select.
 */
public record MultipleChoiceQuestionResponse(
        UUID id,
        UUID meetingId,
        String prompt,
        int position,
        String option1,
        String option2,
        String option3,
        String option4,
        Boolean allowMultiple) implements QuestionResponse {
}

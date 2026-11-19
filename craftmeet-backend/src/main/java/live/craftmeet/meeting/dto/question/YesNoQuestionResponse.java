package live.craftmeet.meeting.dto.question;

import java.util.UUID;

/**
 * A yes/no question returned to clients, with no extra configuration.
 */
public record YesNoQuestionResponse(
		UUID id,
		UUID meetingId,
		String prompt,
		int position) implements QuestionResponse {
}

package live.craftmeet.meeting.dto.question;

import java.util.UUID;

/**
 * A free-text question returned to clients, with a maximum response length.
 */
public record LongAnswerQuestionResponse(
		UUID id,
		UUID meetingId,
		String prompt,
		int position,
		Integer maxLength) implements QuestionResponse {
}

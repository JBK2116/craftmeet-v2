package live.craftmeet.meeting.dto.question;

import java.util.UUID;

/**
 * A ranked-voting question returned to clients, with up to four items.
 */
public record RankedVotingQuestionResponse(
		UUID id,
		UUID meetingId,
		String prompt,
		int position,
		String item1,
		String item2,
		String item3,
		String item4) implements QuestionResponse {
}

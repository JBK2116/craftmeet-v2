package live.craftmeet.meeting.dto.question;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import live.craftmeet.meeting.utils.Constants;

/**
 * Create-question request for a ranked-voting question with up to four items.
 */
public record RankedVotingQuestionRequest(
		@NotBlank @Size(min = Constants.MIN_QUESTION_TEXT_LENGTH, max = Constants.MAX_QUESTION_TEXT_LENGTH) String prompt,

		@Min(1) int position,

		@NotBlank @Size(min = Constants.MIN_MULTIPLE_OPTION_TEXT_LENGTH, max = Constants.MAX_MULTIPLE_OPTION_TEXT_LENGTH) String item1,

		@NotBlank @Size(min = Constants.MIN_MULTIPLE_OPTION_TEXT_LENGTH, max = Constants.MAX_MULTIPLE_OPTION_TEXT_LENGTH) String item2,

		@Size(min = Constants.MIN_MULTIPLE_OPTION_TEXT_LENGTH, max = Constants.MAX_MULTIPLE_OPTION_TEXT_LENGTH) String item3,

		@Size(min = Constants.MIN_MULTIPLE_OPTION_TEXT_LENGTH, max = Constants.MAX_MULTIPLE_OPTION_TEXT_LENGTH) String item4)
		implements QuestionRequest {
}

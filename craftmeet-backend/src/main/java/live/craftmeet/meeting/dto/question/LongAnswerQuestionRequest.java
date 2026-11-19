package live.craftmeet.meeting.dto.question;

import jakarta.validation.constraints.*;

import live.craftmeet.meeting.utils.Constants;

/**
 * Create-question request for a free-text question with a maximum response
 * length.
 */
public record LongAnswerQuestionRequest(
		@NotBlank @Size(min = Constants.MIN_QUESTION_TEXT_LENGTH, max = Constants.MAX_QUESTION_TEXT_LENGTH) String prompt,

		@Min(1) int position,

		@NotNull @Min(Constants.MIN_LONG_ANSWER_TEXT_LENGTH) @Max(Constants.MAX_LONG_ANSWER_TEXT_LENGTH) Integer maxLength)
		implements QuestionRequest {
}

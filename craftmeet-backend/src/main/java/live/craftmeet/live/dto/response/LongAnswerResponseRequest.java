package live.craftmeet.live.dto.response;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import live.craftmeet.meeting.utils.Constants;

/**
 * Response submission for a free-text long-answer question.
 */
public record LongAnswerResponseRequest(
		@NotNull UUID questionId,

		@NotNull UUID participantId,

		@NotBlank @Size(min = Constants.MIN_LONG_ANSWER_TEXT_LENGTH, max = Constants.MAX_LONG_ANSWER_TEXT_LENGTH) String content)
		implements ResponseRequest {
}

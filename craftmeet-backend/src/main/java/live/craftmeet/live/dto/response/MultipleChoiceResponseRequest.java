package live.craftmeet.live.dto.response;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

/**
 * Response submission for a multiple-choice question, recording which options
 * (1-4) were selected.
 */
public record MultipleChoiceResponseRequest(
		@NotNull UUID questionId,

		@NotNull UUID participantId,

		Boolean selected1,

		Boolean selected2,

		Boolean selected3,

		Boolean selected4) implements ResponseRequest {
}

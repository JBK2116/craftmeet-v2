package live.craftmeet.live.dto.response;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

/**
 * Response submission for a yes/no question.
 */
public record YesNoResponseRequest(
		@NotNull UUID questionId,

		@NotNull UUID participantId,

		@NotNull Boolean value) implements ResponseRequest {
}

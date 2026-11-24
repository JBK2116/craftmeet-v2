package live.craftmeet.live.dto.response;

import java.util.UUID;

/**
 * A multiple-choice response returned to clients, recording which options (1-4)
 * were selected.
 */
public record MultipleChoiceResponseResponse(
		UUID id,
		UUID questionId,
		UUID participantId,
		Boolean selected1,
		Boolean selected2,
		Boolean selected3,
		Boolean selected4) implements ResponseResponse {
}

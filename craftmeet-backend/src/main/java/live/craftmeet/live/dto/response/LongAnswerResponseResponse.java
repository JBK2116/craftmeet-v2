package live.craftmeet.live.dto.response;

import java.util.UUID;

/**
 * A free-text long-answer response returned to clients.
 */
public record LongAnswerResponseResponse(
		UUID id,
		UUID questionId,
		UUID participantId,
		String content) implements ResponseResponse {
}

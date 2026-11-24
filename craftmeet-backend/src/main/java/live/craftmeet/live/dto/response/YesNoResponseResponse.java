package live.craftmeet.live.dto.response;

import java.util.UUID;

/**
 * A yes/no response returned to clients.
 */
public record YesNoResponseResponse(
		UUID id,
		UUID questionId,
		UUID participantId,
		Boolean value) implements ResponseResponse {
}

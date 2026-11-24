package live.craftmeet.live.dto.response;

import java.util.UUID;

/**
 * A rating-scale response returned to clients.
 */
public record RatingScaleResponseResponse(
		UUID id,
		UUID questionId,
		UUID participantId,
		Integer value) implements ResponseResponse {
}

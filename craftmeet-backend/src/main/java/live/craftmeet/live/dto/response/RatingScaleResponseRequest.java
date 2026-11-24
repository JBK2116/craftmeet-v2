package live.craftmeet.live.dto.response;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import live.craftmeet.meeting.utils.Constants;

/**
 * Response submission for a rating-scale question, bounded by the scale range.
 */
public record RatingScaleResponseRequest(
		@NotNull UUID questionId,

		@NotNull UUID participantId,

		@NotNull @Min(Constants.MIN_RATING_SCALE_VALUE) @Max(Constants.MAX_RATING_SCALE_VALUE) Integer value)
		implements ResponseRequest {
}

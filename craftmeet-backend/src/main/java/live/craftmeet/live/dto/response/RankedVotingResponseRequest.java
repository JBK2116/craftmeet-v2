package live.craftmeet.live.dto.response;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Response submission for a ranked-voting question, assigning each rank
 * position (1-4) an item number. {@code rank1} is the top priority; {@code rank3}
 * and {@code rank4} mirror the optional items.
 */
public record RankedVotingResponseRequest(
		@NotNull UUID questionId,

		@NotNull UUID participantId,

		@NotNull @Min(1) @Max(4) Integer rank1,

		@NotNull @Min(1) @Max(4) Integer rank2,

		@Min(1) @Max(4) Integer rank3,

		@Min(1) @Max(4) Integer rank4) implements ResponseRequest {
}

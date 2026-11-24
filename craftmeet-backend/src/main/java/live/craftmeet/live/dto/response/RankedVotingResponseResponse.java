package live.craftmeet.live.dto.response;

import java.util.UUID;

/**
 * A ranked-voting response returned to clients, assigning each rank position
 * (1-4) an item number.
 */
public record RankedVotingResponseResponse(
		UUID id,
		UUID questionId,
		UUID participantId,
		Integer rank1,
		Integer rank2,
		Integer rank3,
		Integer rank4) implements ResponseResponse {
}

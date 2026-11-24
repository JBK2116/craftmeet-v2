package live.craftmeet.live.dto.response;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import live.craftmeet.live.model.*;

/**
 * Common contract for every response returned to clients. Jackson polymorphic
 * serialization writes the {@code type} discriminator, mirroring the
 * {@code response_type} discriminator of the entity hierarchy.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = MultipleChoiceResponseResponse.class, name = "multiple_choice"),
		@JsonSubTypes.Type(value = LongAnswerResponseResponse.class, name = "long_answer"),
		@JsonSubTypes.Type(value = RankedVotingResponseResponse.class, name = "ranked_voting"),
		@JsonSubTypes.Type(value = RatingScaleResponseResponse.class, name = "rating_scale"),
		@JsonSubTypes.Type(value = YesNoResponseResponse.class, name = "yes_no")
})
public sealed interface ResponseResponse
		permits MultipleChoiceResponseResponse, LongAnswerResponseResponse,
		RankedVotingResponseResponse, RatingScaleResponseResponse, YesNoResponseResponse {

	UUID id();

	UUID questionId();

	UUID participantId();

	/**
	 * Maps a persisted {@link Response} to its concrete response subtype by
	 * dispatching on the runtime entity type.
	 */
	static ResponseResponse from(Response response) {
		if (response instanceof MultipleChoiceResponse r) {
			return new MultipleChoiceResponseResponse(
					response.getId(), response.getQuestionId(), response.getParticipantId(),
					r.getSelected1(), r.getSelected2(), r.getSelected3(), r.getSelected4());
		}
		if (response instanceof LongAnswerResponse r) {
			return new LongAnswerResponseResponse(
					response.getId(), response.getQuestionId(), response.getParticipantId(),
					r.getContent());
		}
		if (response instanceof RankedVotingResponse r) {
			return new RankedVotingResponseResponse(
					response.getId(), response.getQuestionId(), response.getParticipantId(),
					r.getRank1(), r.getRank2(), r.getRank3(), r.getRank4());
		}
		if (response instanceof RatingScaleResponse r) {
			return new RatingScaleResponseResponse(
					response.getId(), response.getQuestionId(), response.getParticipantId(),
					r.getValue());
		}
		if (response instanceof YesNoResponse r) {
			return new YesNoResponseResponse(
					response.getId(), response.getQuestionId(), response.getParticipantId(),
					r.getValue());
		}
		throw new IllegalArgumentException("Unknown response type: " + response.getClass().getSimpleName());
	}
}

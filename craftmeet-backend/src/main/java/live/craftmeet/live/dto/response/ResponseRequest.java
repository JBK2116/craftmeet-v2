package live.craftmeet.live.dto.response;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Common contract for every response submission. Jackson polymorphic
 * deserialization selects the concrete subtype from the {@code type} field,
 * mirroring the {@code response_type} discriminator of the entity hierarchy.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = MultipleChoiceResponseRequest.class, name = "multiple_choice"),
		@JsonSubTypes.Type(value = LongAnswerResponseRequest.class, name = "long_answer"),
		@JsonSubTypes.Type(value = RankedVotingResponseRequest.class, name = "ranked_voting"),
		@JsonSubTypes.Type(value = RatingScaleResponseRequest.class, name = "rating_scale"),
		@JsonSubTypes.Type(value = YesNoResponseRequest.class, name = "yes_no")
})
public sealed interface ResponseRequest
		permits MultipleChoiceResponseRequest, LongAnswerResponseRequest,
		RankedVotingResponseRequest, RatingScaleResponseRequest, YesNoResponseRequest {

	UUID questionId();

	UUID participantId();
}

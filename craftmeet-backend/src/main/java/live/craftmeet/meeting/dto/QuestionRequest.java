package live.craftmeet.meeting.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Common contract for every create-question request. Jackson polymorphic
 * deserialization selects the concrete subtype from the {@code type} field,
 * mirroring the {@code question_type} discriminator of the entity hierarchy.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MultipleChoiceQuestionRequest.class, name = "multiple_choice"),
        @JsonSubTypes.Type(value = LongAnswerQuestionRequest.class, name = "long_answer"),
        @JsonSubTypes.Type(value = RankedVotingQuestionRequest.class, name = "ranked_voting"),
        @JsonSubTypes.Type(value = RatingScaleQuestionRequest.class, name = "rating_scale"),
        @JsonSubTypes.Type(value = YesNoQuestionRequest.class, name = "yes_no")
})
public sealed interface QuestionRequest
        permits MultipleChoiceQuestionRequest, LongAnswerQuestionRequest,
        RankedVotingQuestionRequest, RatingScaleQuestionRequest, YesNoQuestionRequest {

    String prompt();

    int position();
}

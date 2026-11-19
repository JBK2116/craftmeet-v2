package live.craftmeet.meeting.dto.question;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import live.craftmeet.meeting.model.question.*;

/**
 * Common contract for every question returned to clients. Jackson polymorphic
 * serialization writes the {@code type} discriminator, mirroring the
 * {@code question_type} discriminator of the entity hierarchy.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = MultipleChoiceQuestionResponse.class, name = "multiple_choice"),
		@JsonSubTypes.Type(value = LongAnswerQuestionResponse.class, name = "long_answer"),
		@JsonSubTypes.Type(value = RankedVotingQuestionResponse.class, name = "ranked_voting"),
		@JsonSubTypes.Type(value = RatingScaleQuestionResponse.class, name = "rating_scale"),
		@JsonSubTypes.Type(value = YesNoQuestionResponse.class, name = "yes_no")
})
public sealed interface QuestionResponse
		permits MultipleChoiceQuestionResponse, LongAnswerQuestionResponse,
		RankedVotingQuestionResponse, RatingScaleQuestionResponse, YesNoQuestionResponse {

	UUID id();

	UUID meetingId();

	String prompt();

	int position();

	/**
	 * Maps a persisted {@link Question} to its concrete response subtype by
	 * dispatching on the runtime entity type.
	 */
	static QuestionResponse from(Question question) {
		if (question instanceof MultipleChoiceQuestion q) {
			return new MultipleChoiceQuestionResponse(
					question.getId(), question.getMeetingId(), question.getPrompt(), question.getPosition(),
					q.getOption1(), q.getOption2(), q.getOption3(), q.getOption4(), q.getAllowMultiple());
		}
		if (question instanceof LongAnswerQuestion q) {
			return new LongAnswerQuestionResponse(
					question.getId(), question.getMeetingId(), question.getPrompt(), question.getPosition(),
					q.getMaxLength());
		}
		if (question instanceof RankedVotingQuestion q) {
			return new RankedVotingQuestionResponse(
					question.getId(), question.getMeetingId(), question.getPrompt(), question.getPosition(),
					q.getItem1(), q.getItem2(), q.getItem3(), q.getItem4());
		}
		if (question instanceof RatingScaleQuestion q) {
			return new RatingScaleQuestionResponse(
					question.getId(), question.getMeetingId(), question.getPrompt(), question.getPosition(),
					q.getMin(), q.getMax());
		}
		if (question instanceof YesNoQuestion) {
			return new YesNoQuestionResponse(
					question.getId(), question.getMeetingId(), question.getPrompt(), question.getPosition());
		}
		throw new IllegalArgumentException("Unknown question type: " + question.getClass().getSimpleName());
	}
}

package live.craftmeet.meeting.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import live.craftmeet.meeting.annotation.ContiguousPositions;
import live.craftmeet.meeting.utils.Constants;

/**
 * Request payload for creating a meeting with its settings.
 */
public record MeetingRequest(
		@NotBlank(message = "Title is required") @Size(min = Constants.MIN_TITLE_LENGTH, max = Constants.MAX_TITLE_LENGTH, message = "Title must be between {min} and {max} characters") String title,

		@Size(min = Constants.MIN_DESCRIPTION_LENGTH, max = Constants.MAX_DESCRIPTION_LENGTH, message = "Description must be between {min} and {max} characters") String description,

		@NotNull(message = "Duration is required") @Min(value = Constants.MIN_MEETING_DURATION, message = "Duration must be at least {value}") @Max(value = Constants.MAX_MEETING_DURATION, message = "Duration must be at most {value}") Integer duration,

		@NotNull(message = "Participant cap is required") @Min(value = Constants.MIN_PARTICIPANT_COUNT, message = "Participant cap must be at least {value}") @Max(value = Constants.MAX_PARTICIPANT_COUNT, message = "Participant cap must be at most {value}") Integer participantCap,

		@NotNull(message = "Questions are required") @Valid @ContiguousPositions @Size(min = 1, max = Constants.MAX_QUESTION_COUNT, message = "A meeting must have between {min} and {max} questions") List<QuestionRequest> questions) {

}

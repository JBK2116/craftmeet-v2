package live.craftmeet.meeting.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import live.craftmeet.meeting.utils.Constants;

/**
 * Request payload for creating a meeting with its settings.
 */
public record CreateMeetingRequest(
        @NotBlank(message = "Title is required") @Size(min = Constants.MIN_TITLE_LENGTH, max = Constants.MAX_TITLE_LENGTH, message = "Title must be between {min} and {max} characters") String title,

        @Size(min = Constants.MIN_DESCRIPTION_LENGTH, max = Constants.MAX_DESCRIPTION_LENGTH, message = "Description must be between {min} and {max} characters") String description,

        @NotNull(message = "Duration is required") @Min(value = Constants.MIN_MEETING_DURATION, message = "Duration must be at least {value}") @Max(value = Constants.MAX_MEETING_DURATION, message = "Duration must be at most {value}") Integer duration,

        @NotNull(message = "Participant cap is required") @Min(value = Constants.MIN_PARTICIPANT_COUNT, message = "Participant cap must be at least {value}") @Max(value = Constants.MAX_PARTICIPANT_COUNT, message = "Participant cap must be at most {value}") Integer participantCap,

        @Valid @Size(min = 1, message = "At least one question is required") List<CreateQuestionRequest> questions) {

}

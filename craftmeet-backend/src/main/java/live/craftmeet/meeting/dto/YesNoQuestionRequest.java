package live.craftmeet.meeting.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import live.craftmeet.meeting.utils.Constants;

/**
 * Create-question request for a yes/no question with no extra configuration.
 */
public record YesNoQuestionRequest(
        @NotBlank
        @Size(min = Constants.MIN_QUESTION_TEXT_LENGTH, max = Constants.MAX_QUESTION_TEXT_LENGTH)
        String prompt,

        @Min(1)
        int position) implements CreateQuestionRequest {
}

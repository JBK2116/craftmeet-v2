package live.craftmeet.meeting.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import live.craftmeet.meeting.utils.Constants;

/**
 * Create-question request for a multiple-choice question with up to four
 * options and optional multi-select.
 */
public record MultipleChoiceQuestionRequest(
        @NotBlank
        @Size(min = Constants.MIN_QUESTION_TEXT_LENGTH, max = Constants.MAX_QUESTION_TEXT_LENGTH)
        String prompt,

        @Min(1)
        int position,

        @NotBlank
        @Size(min = Constants.MIN_MULTIPLE_OPTION_TEXT_LENGTH, max = Constants.MAX_MULTIPLE_OPTION_TEXT_LENGTH)
        String option1,

        @NotBlank
        @Size(min = Constants.MIN_MULTIPLE_OPTION_TEXT_LENGTH, max = Constants.MAX_MULTIPLE_OPTION_TEXT_LENGTH)
        String option2,

        @Size(min = Constants.MIN_MULTIPLE_OPTION_TEXT_LENGTH, max = Constants.MAX_MULTIPLE_OPTION_TEXT_LENGTH)
        String option3,

        @Size(min = Constants.MIN_MULTIPLE_OPTION_TEXT_LENGTH, max = Constants.MAX_MULTIPLE_OPTION_TEXT_LENGTH)
        String option4,

        Boolean allowMultiple) implements CreateQuestionRequest {
}

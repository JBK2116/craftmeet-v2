package live.craftmeet.meeting.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import live.craftmeet.meeting.utils.Constants;

/**
 * Create-question request for a free-text question with a maximum response
 * length.
 */
public record LongAnswerQuestionRequest(
        @NotBlank
        @Size(min = Constants.MIN_QUESTION_TEXT_LENGTH, max = Constants.MAX_QUESTION_TEXT_LENGTH)
        String prompt,

        @Min(1)
        int position,

        @NotNull
        @Min(Constants.MIN_LONG_ANSWER_TEXT_LENGTH)
        @Max(Constants.MAX_LONG_ANSWER_TEXT_LENGTH)
        Integer maxLength) implements QuestionRequest {
}

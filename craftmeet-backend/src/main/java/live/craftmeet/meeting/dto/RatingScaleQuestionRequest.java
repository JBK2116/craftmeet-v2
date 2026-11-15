package live.craftmeet.meeting.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import live.craftmeet.meeting.utils.Constants;

/**
 * Create-question request for a rating-scale question bounded by a minimum and
 * maximum value.
 */
public record RatingScaleQuestionRequest(
        @NotBlank
        @Size(min = Constants.MIN_QUESTION_TEXT_LENGTH, max = Constants.MAX_QUESTION_TEXT_LENGTH)
        String prompt,

        @Min(1)
        int position,

        @NotNull
        @Min(Constants.MIN_RATING_SCALE_VALUE)
        @Max(Constants.MAX_RATING_SCALE_VALUE)
        Integer min,

        @NotNull
        @Min(Constants.MIN_RATING_SCALE_VALUE)
        @Max(Constants.MAX_RATING_SCALE_VALUE)
        Integer max) implements CreateQuestionRequest {
}

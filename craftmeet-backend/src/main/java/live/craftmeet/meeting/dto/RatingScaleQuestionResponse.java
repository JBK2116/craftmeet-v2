package live.craftmeet.meeting.dto;

import java.util.UUID;

/**
 * A rating-scale question returned to clients, bounded by a minimum and maximum
 * value.
 */
public record RatingScaleQuestionResponse(
        UUID id,
        UUID meetingId,
        String prompt,
        int position,
        Integer min,
        Integer max) implements QuestionResponse {
}

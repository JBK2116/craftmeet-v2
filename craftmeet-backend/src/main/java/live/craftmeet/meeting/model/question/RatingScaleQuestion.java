package live.craftmeet.meeting.model.question;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import live.craftmeet.meeting.utils.Constants;
import lombok.*;

/**
 * A rating-scale question bounded by a minimum and maximum value.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("rating_scale")
public final class RatingScaleQuestion extends Question {

    @Column(name = "min_value",
            check = @CheckConstraint(name = "ck_question_min_value",
                    constraint = "min_value BETWEEN " + Constants.MIN_RATING_SCALE_VALUE + " AND " + Constants.MAX_RATING_SCALE_VALUE))
    private Integer min;

    @Column(name = "max_value",
            check = @CheckConstraint(name = "ck_question_max_value",
                    constraint = "max_value BETWEEN " + Constants.MIN_RATING_SCALE_VALUE + " AND " + Constants.MAX_RATING_SCALE_VALUE))
    private Integer max;
}

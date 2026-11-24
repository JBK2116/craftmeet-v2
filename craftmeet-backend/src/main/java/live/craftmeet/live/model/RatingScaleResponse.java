package live.craftmeet.live.model;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import live.craftmeet.meeting.utils.Constants;
import lombok.*;

/**
 * A rating-scale response storing a single integer rating within the scale
 * range.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("rating_scale")
public class RatingScaleResponse extends Response {

	@Column(name = "rating_value",
			check = @CheckConstraint(name = "ck_response_rating_value",
					constraint = "rating_value BETWEEN " + Constants.MIN_RATING_SCALE_VALUE + " AND " + Constants.MAX_RATING_SCALE_VALUE))
	private Integer value;
}

package live.craftmeet.live.model.response;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import live.craftmeet.meeting.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A free-text response to a long-answer question, bounded by the configured
 * maximum length.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("long_answer")
public class LongAnswerResponse extends Response {

	@Column(name = "content", length = Constants.MAX_LONG_ANSWER_TEXT_LENGTH)
	private String content;
}

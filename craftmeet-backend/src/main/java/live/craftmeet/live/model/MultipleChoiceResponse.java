package live.craftmeet.live.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import lombok.*;

/**
 * A multiple-choice response recording which options (1-4) a participant
 * selected. Supports multi-select when the question allows it.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("multiple_choice")
public class MultipleChoiceResponse extends Response {

	@Column(name = "selected_1")
	private Boolean selected1;

	@Column(name = "selected_2")
	private Boolean selected2;

	@Column(name = "selected_3")
	private Boolean selected3;

	@Column(name = "selected_4")
	private Boolean selected4;
}

package live.craftmeet.live.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import lombok.*;

/**
 * A yes/no response storing a boolean vote.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("yes_no")
public class YesNoResponse extends Response {

	@Column(name = "yes_no_value")
	private Boolean value;
}

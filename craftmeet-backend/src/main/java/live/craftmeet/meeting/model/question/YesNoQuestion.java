package live.craftmeet.meeting.model.question;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import lombok.*;

/**
 * A yes/no question with no additional configuration.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("yes_no")
public class YesNoQuestion extends Question {
}

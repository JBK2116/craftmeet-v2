package live.craftmeet.meeting.model.question;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import live.craftmeet.meeting.utils.Constants;
import lombok.*;

/**
 * A free-text question with a maximum response length.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("long_answer")
public final class LongAnswerQuestion extends Question {

    @Column(name = "max_length", check = @CheckConstraint(name = "ck_question_max_length", constraint = "max_length BETWEEN "
            + Constants.MIN_LONG_ANSWER_TEXT_LENGTH + " AND " + Constants.MAX_LONG_ANSWER_TEXT_LENGTH))
    private Integer maxLength;
}

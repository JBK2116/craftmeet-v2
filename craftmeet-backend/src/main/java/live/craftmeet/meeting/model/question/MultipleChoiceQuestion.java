package live.craftmeet.meeting.model.question;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import live.craftmeet.meeting.utils.Constants;
import lombok.*;

/**
 * A multiple-choice question with up to four options and optional
 * multi-select.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("multiple_choice")
public class MultipleChoiceQuestion extends Question {

    @Column(name = "option_1", length = Constants.MAX_MULTIPLE_OPTION_TEXT_LENGTH)
    private String option1;

    @Column(name = "option_2", length = Constants.MAX_MULTIPLE_OPTION_TEXT_LENGTH)
    private String option2;

    @Column(name = "option_3", length = Constants.MAX_MULTIPLE_OPTION_TEXT_LENGTH)
    private String option3;

    @Column(name = "option_4", length = Constants.MAX_MULTIPLE_OPTION_TEXT_LENGTH)
    private String option4;

    @Column(name = "allow_multiple")
    private Boolean allowMultiple;
}

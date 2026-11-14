package live.craftmeet.meeting.model.question;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import live.craftmeet.meeting.utils.Constants;
import lombok.*;

/**
 * A ranked-voting question with up to four items to prioritize.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("ranked_voting")
public final class RankedVotingQuestion extends Question {

    @Column(name = "item_1", length = Constants.MAX_MULTIPLE_OPTION_TEXT_LENGTH)
    private String item1;

    @Column(name = "item_2", length = Constants.MAX_MULTIPLE_OPTION_TEXT_LENGTH)
    private String item2;

    @Column(name = "item_3", length = Constants.MAX_MULTIPLE_OPTION_TEXT_LENGTH)
    private String item3;

    @Column(name = "item_4", length = Constants.MAX_MULTIPLE_OPTION_TEXT_LENGTH)
    private String item4;
}

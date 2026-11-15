package live.craftmeet.meeting.model.question;

import java.util.UUID;

import jakarta.persistence.*;

import live.craftmeet.meeting.utils.Constants;
import live.craftmeet.persistence.BaseEntity;
import lombok.*;

/**
 * Base entity for every question type in a meeting. Uses single-table
 * inheritance: all question types share the {@code questions} table and are
 * distinguished by the {@code question_type} discriminator column.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "question_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "questions")
public abstract class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(name = "meeting_id", nullable = false)
    private UUID meetingId;

    @Column(name = "prompt", nullable = false, length = Constants.MAX_QUESTION_TEXT_LENGTH)
    private String prompt;

    @Column(name = "position", nullable = false,
            check = @CheckConstraint(name = "ck_question_position", constraint = "position >= 1"))
    private int position;
}

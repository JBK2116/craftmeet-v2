package live.craftmeet.live.model.response;

import java.util.UUID;

import jakarta.persistence.*;

import live.craftmeet.persistence.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Base entity for every response type in a meeting. Uses single-table
 * inheritance: all response types share the {@code responses} table and are
 * distinguished by the {@code response_type} discriminator column.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "response_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "responses")
public abstract class Response extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Setter(AccessLevel.NONE)
	private UUID id;

	@Column(name = "question_id", nullable = false)
	private UUID questionId;

	@Column(name = "participant_id", nullable = false)
	private UUID participantId;

}

package live.craftmeet.live.model.participant;

import java.util.UUID;

import jakarta.persistence.*;

import live.craftmeet.live.utils.Constants;
import live.craftmeet.persistence.BaseEntity;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "participants")
public class Participant extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Setter(AccessLevel.NONE)
	private UUID id;

	@Column(name = "meeting_id", nullable = false)
	private UUID meetingId;

	@Column(name = "name", nullable = false, length = Constants.MAX_PARTICIPANT_NAME_LENGTH)
	private String name;
}

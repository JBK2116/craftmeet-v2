package live.craftmeet.meeting.model.meeting;

import java.util.UUID;

import jakarta.persistence.*;

import live.craftmeet.meeting.utils.Constants;
import live.craftmeet.persistence.BaseEntity;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "meetings")
public class Meeting extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "title", nullable = false, length = Constants.MAX_TITLE_LENGTH)
    private String title;

    @Column(name = "description", nullable = true, length = Constants.MAX_DESCRIPTION_LENGTH)
    private String description;

    @Column(name = "room_code", nullable = false, unique = true, length = Constants.MEETING_CODE_LENGTH)
    private String roomCode;

    @Embedded
    private MeetingSettings settings;

    @Embedded
    private MeetingSession session;

    @Embedded
    private MeetingExport export;
}

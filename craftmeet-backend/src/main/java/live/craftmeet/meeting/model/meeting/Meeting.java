package live.craftmeet.meeting.model.meeting;

import java.util.UUID;

import jakarta.persistence.*;

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

    @Embedded
    private MeetingSettings settings;

    @Embedded
    private MeetingSession session;

    @Embedded
    private MeetingExport export;
}

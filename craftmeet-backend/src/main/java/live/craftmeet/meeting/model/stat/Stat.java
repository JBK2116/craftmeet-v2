package live.craftmeet.meeting.model.stat;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import live.craftmeet.persistence.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stats")
public class Stat extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(name = "meeting_id", nullable = false, unique = true)
    private UUID meetingId;

    @Column(name = "total_participants", nullable = false)
    private int totalParticipants;

    @Column(name = "total_questions_asked", nullable = false)
    private int totalQuestionsAsked;

    @Column(name = "total_responses_received", nullable = false)
    private int totalResponsesReceived;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;
}

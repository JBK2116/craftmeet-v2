package live.craftmeet.meeting.model.meeting;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import live.craftmeet.meeting.utils.Constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingSettings {
    @Column(name = "duration", nullable = false,
            check = @CheckConstraint(name = "ck_meeting_duration",
                    constraint = "duration BETWEEN " + Constants.MIN_MEETING_DURATION + " AND " + Constants.MAX_MEETING_DURATION))
    private int duration;

    @Column(name = "participant_cap", nullable = false,
            check = @CheckConstraint(name = "ck_meeting_participant_cap",
                    constraint = "participant_cap BETWEEN " + Constants.MIN_PARTICIPANT_COUNT + " AND " + Constants.MAX_PARTICIPANT_COUNT))
    private int participantCap;

}

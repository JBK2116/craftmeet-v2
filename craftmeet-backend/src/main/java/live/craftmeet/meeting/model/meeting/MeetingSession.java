package live.craftmeet.meeting.model.meeting;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingSession {
    @Column(name = "start_time", nullable = true)
    private Instant startTime;

    @Column(name = "end_time", nullable = true)
    private Instant endTime;

}

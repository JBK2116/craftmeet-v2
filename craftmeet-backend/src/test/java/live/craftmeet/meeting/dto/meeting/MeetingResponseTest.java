package live.craftmeet.meeting.dto.meeting;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import live.craftmeet.meeting.model.meeting.Meeting;
import live.craftmeet.meeting.model.meeting.MeetingSettings;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link MeetingResponse#from}, focused on the null-session case:
 * Hibernate loads an all-null {@code @Embedded} value object as {@code null}, so
 * a draft meeting (no start/end time) must map to null timestamps rather than
 * throwing a NullPointerException.
 */
class MeetingResponseTest {

	@Test
	void from_mapsNullSessionToNullTimestamps() {
		Meeting meeting = new Meeting();
		meeting.setUserId(UUID.randomUUID());
		meeting.setTitle("Draft meeting");
		meeting.setRoomCode("00000001");
		meeting.setSettings(new MeetingSettings(30, 10));
		// session intentionally left null — mirrors Hibernate on a draft meeting

		MeetingResponse response = MeetingResponse.from(meeting, List.of(), null);

		assertThat(response.startTime()).isNull();
		assertThat(response.endTime()).isNull();
		assertThat(response.duration()).isEqualTo(30);
		assertThat(response.participantCap()).isEqualTo(10);
	}
}

package live.craftmeet.meeting.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import live.craftmeet.meeting.model.meeting.Meeting;

/**
 * A meeting returned to clients, including its settings, session timestamps,
 * and questions.
 */
public record MeetingResponse(
		UUID id,
		UUID userId,
		String title,
		String description,
		String roomCode,
		int duration,
		int participantCap,
		Instant startTime,
		Instant endTime,
		Instant createdAt,
		Instant updatedAt,
		List<QuestionResponse> questions,
		StatResponse statResponse) {

	/**
	 * Maps a persisted {@link Meeting} to a response, with its questions already
	 * converted.
	 */
	public static MeetingResponse from(Meeting meeting, List<QuestionResponse> questions, StatResponse statResponse) {
		return new MeetingResponse(
				meeting.getId(),
				meeting.getUserId(),
				meeting.getTitle(),
				meeting.getDescription(),
				meeting.getRoomCode(),
				meeting.getSettings().getDuration(),
				meeting.getSettings().getParticipantCap(),
				meeting.getSession().getStartTime(),
				meeting.getSession().getEndTime(),
				meeting.getCreatedAt(),
				meeting.getUpdatedAt(),
				questions, statResponse);
	}
}

package live.craftmeet.meeting.dto.meeting;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import live.craftmeet.meeting.dto.question.QuestionResponse;
import live.craftmeet.meeting.dto.stat.StatResponse;
import live.craftmeet.meeting.model.meeting.Meeting;
import live.craftmeet.meeting.model.meeting.MeetingSession;

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
		MeetingSession session = meeting.getSession();
		return new MeetingResponse(
				meeting.getId(),
				meeting.getUserId(),
				meeting.getTitle(),
				meeting.getDescription(),
				meeting.getRoomCode(),
				meeting.getSettings().getDuration(),
				meeting.getSettings().getParticipantCap(),
				session != null ? session.getStartTime() : null,
				session != null ? session.getEndTime() : null,
				meeting.getCreatedAt(),
				meeting.getUpdatedAt(),
				questions, statResponse);
	}
}

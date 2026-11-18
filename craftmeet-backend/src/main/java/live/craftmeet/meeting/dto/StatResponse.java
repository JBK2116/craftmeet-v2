package live.craftmeet.meeting.dto;

import java.util.UUID;

import live.craftmeet.meeting.model.stat.Stat;

public record StatResponse(
		UUID id,
		UUID meetingId,
		int totalParticipants,
		int totalQuestionsAsked,
		int totalResponsesReceived,
		Integer durationSeconds) {
	public static StatResponse from(Stat stat) {
		return new StatResponse(
				stat.getId(),
				stat.getMeetingId(),
				stat.getTotalParticipants(),
				stat.getTotalQuestionsAsked(),
				stat.getTotalResponsesReceived(),
				stat.getDurationSeconds());
	}
}
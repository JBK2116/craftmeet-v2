package live.craftmeet.meeting.dto.stat;

public record StatLifeTimeResponse(
		int totalParticipants,
		int totalQuestionsAsked,
		int totalResponsesReceived) {
}

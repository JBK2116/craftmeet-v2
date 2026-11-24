package live.craftmeet.live.dto.participant;

import java.util.UUID;

import live.craftmeet.live.model.participant.Participant;

public record ParticipantResponse(
		UUID id,
		UUID meetingId,
		String name) {

	public static ParticipantResponse from(Participant participant) {
		return new ParticipantResponse(
				participant.getId(),
				participant.getMeetingId(),
				participant.getName());
	}
}

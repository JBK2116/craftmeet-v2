package live.craftmeet.live.exception;

import java.util.UUID;

public class ParticipantAuthenticationError extends RuntimeException {
    public ParticipantAuthenticationError(UUID meetingId) {
        super("participant is not authenticated for meeting: " + meetingId);
    }
}

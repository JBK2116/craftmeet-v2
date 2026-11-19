package live.craftmeet.meeting.exception;

import java.util.UUID;

import live.craftmeet.exception.BaseException;
import live.craftmeet.exception.ErrorCode;

public class StatNotFoundError extends BaseException {
	public StatNotFoundError(UUID meetingId) {
		super("Stats not found for meeting with ID: " + meetingId.toString(), ErrorCode.INTERNAL_ERROR);
	}
}

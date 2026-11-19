package live.craftmeet.meeting.exception;

import java.util.UUID;

import live.craftmeet.exception.BaseException;
import live.craftmeet.exception.ErrorCode;

public class MeetingNotFoundError extends BaseException {
	public MeetingNotFoundError(UUID meetingId) {
		super("Meeting not found with ID " + meetingId.toString(), ErrorCode.RESOURCE_NOT_FOUND);
	}
}

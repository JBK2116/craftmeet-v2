package live.craftmeet.meeting.exception;

import java.util.UUID;

import live.craftmeet.exception.BaseException;
import live.craftmeet.exception.ErrorCode;

public class MeetingNotEditableError extends BaseException {
	public MeetingNotEditableError(UUID meetingId) {
		super("Meeting with ID " + meetingId.toString() + " can no longer be edited", ErrorCode.CONFLICT);
	}
}

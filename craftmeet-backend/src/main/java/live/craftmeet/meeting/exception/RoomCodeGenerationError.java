package live.craftmeet.meeting.exception;

import live.craftmeet.exception.BaseException;
import live.craftmeet.exception.ErrorCode;

public class RoomCodeGenerationError extends BaseException {
	public RoomCodeGenerationError() {
		super("Failed to generate room code for meeting", ErrorCode.INTERNAL_ERROR);
	}

}

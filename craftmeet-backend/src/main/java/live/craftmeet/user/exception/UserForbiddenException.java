package live.craftmeet.user.exception;

import java.util.UUID;

import live.craftmeet.exception.BaseException;
import live.craftmeet.exception.ErrorCode;

public class UserForbiddenException extends BaseException {
	public UserForbiddenException(UUID userid) {
		super("User with ID " + userid + " is forbidden from performing this action.", ErrorCode.FORBIDDEN);
	}
}

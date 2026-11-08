package live.craftmeet.user.exception;

import live.craftmeet.exception.BaseException;
import live.craftmeet.exception.ErrorCode;

public class UserNotVerifiedException extends BaseException {
    public UserNotVerifiedException(String email) {
        super("User with email " + email + " is not verified.", ErrorCode.FORBIDDEN);
    }
}

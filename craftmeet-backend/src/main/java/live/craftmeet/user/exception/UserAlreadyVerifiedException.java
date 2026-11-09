package live.craftmeet.user.exception;

import live.craftmeet.exception.BaseException;
import live.craftmeet.exception.ErrorCode;

public class UserAlreadyVerifiedException extends BaseException {
    public UserAlreadyVerifiedException(String email) {
        super("User with email " + email + " is already verified.", ErrorCode.DUPLICATE_RESOURCE);
    }
}

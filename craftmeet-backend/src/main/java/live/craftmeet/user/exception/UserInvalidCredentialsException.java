package live.craftmeet.user.exception;

import live.craftmeet.exception.BaseException;
import live.craftmeet.exception.ErrorCode;

public class UserInvalidCredentialsException extends BaseException {
    public UserInvalidCredentialsException() {
        super("Invalid credentials provided", ErrorCode.VALIDATION_FAILED);
    }

}

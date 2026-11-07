package live.craftmeet.user.exception;

import live.craftmeet.exception.BaseException;
import live.craftmeet.exception.ErrorCode;

import java.util.UUID;

public class UserAlreadyExistsException extends BaseException {
    public UserAlreadyExistsException(UUID userId) {
        super("User already exists with id" + userId.toString(), ErrorCode.DUPLICATE_RESOURCE);
    }

    public UserAlreadyExistsException(String email) {
        super("User already exists with email" + email, ErrorCode.DUPLICATE_RESOURCE);
    }
}

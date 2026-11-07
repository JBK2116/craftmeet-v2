package live.craftmeet.user.exception;

import live.craftmeet.exception.BaseException;
import live.craftmeet.exception.ErrorCode;

import java.util.UUID;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException(UUID userId) {
        super("User not found with id: " + userId.toString(), ErrorCode.RESOURCE_NOT_FOUND);
    }
}
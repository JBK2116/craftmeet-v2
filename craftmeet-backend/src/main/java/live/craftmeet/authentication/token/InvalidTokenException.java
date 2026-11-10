package live.craftmeet.authentication.token;

import live.craftmeet.exception.BaseException;
import live.craftmeet.exception.ErrorCode;

public class InvalidTokenException extends BaseException {
    public InvalidTokenException(TokenType tokenType) {
        super("Invalid " + tokenType.getType() + " token", ErrorCode.UNAUTHORIZED);
    }
}

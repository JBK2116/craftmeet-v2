package live.craftmeet.authentication.jwt;

import live.craftmeet.exception.BaseException;
import live.craftmeet.exception.ErrorCode;

public class InvalidJwtException extends BaseException {
    public InvalidJwtException(JwtType jwtType) {
        super("Invalid " + jwtType.getType() + " token", ErrorCode.UNAUTHORIZED);
    }
}

package live.craftmeet.authentication.token;

import java.util.concurrent.TimeUnit;

import live.craftmeet.exception.BaseException;
import live.craftmeet.exception.ErrorCode;

public class CooldownTokenException extends BaseException {
    public CooldownTokenException(TokenType tokenType, long remainingCooldownMS) {
        super(tokenType + " is still in cooldown. Remaining cooldown: "
                + TimeUnit.MILLISECONDS.toSeconds(remainingCooldownMS) + "seconds",
                ErrorCode.TOO_MANY_REQUESTS);
    }

}

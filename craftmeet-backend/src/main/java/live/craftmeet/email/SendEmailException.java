package live.craftmeet.email;

import live.craftmeet.exception.BaseException;
import live.craftmeet.exception.ErrorCode;

public class SendEmailException extends BaseException {
    public SendEmailException(String message) {
        super(message, ErrorCode.INTERNAL_ERROR);
    }

}

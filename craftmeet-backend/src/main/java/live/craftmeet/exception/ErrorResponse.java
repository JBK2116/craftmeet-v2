package live.craftmeet.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        int status,
        String code,
        String message,
        String path,
        Instant timestamp,
        List<FieldError> fieldErrors

) {
    public record FieldError(String field, String message) {

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static ErrorResponse of(ErrorCode ec, String message, String path) {
        return new ErrorResponse(
                ec.getHttpStatus().value(),
                ec.name(),
                message,
                path,
                Instant.now(),
                null
        );
    }
}

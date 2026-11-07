package live.craftmeet.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBase(BaseException ex, HttpServletRequest req) {
        ErrorCode ec = ex.getErrorCode();
        if (ec.getHttpStatus().is5xxServerError()) {
            log.error("[{}] {} {}", ec, req.getMethod(), req.getRequestURI(), ex);
        } else {
            log.warn("[{}] {} {} - {}", ec, req.getMethod(), req.getRequestURI(), ex.getMessage());
        }
        return ResponseEntity.status(ec.getHttpStatus().value())
                .body(ErrorResponse.of(ec, ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest req) {
        ErrorCode ec = ErrorCode.VALIDATION_FAILED;
        List<ErrorResponse.FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> new ErrorResponse.FieldError(f.getField(), f.getDefaultMessage()))
                .toList();

        log.warn("[{}] {} {} - {}", ec, req.getMethod(), req.getRequestURI(), errors);
        return ResponseEntity.status(ec.getHttpStatus().value())
                .body(new ErrorResponse(ec.getHttpStatus().value(), ec.name(), ec.getMessage(),
                        req.getRequestURI(), Instant.now(), errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest req) {
        ErrorCode ec = ErrorCode.INTERNAL_ERROR;
        log.error("[{}] {} {}", ec, req.getMethod(), req.getRequestURI(), ex);
        return ResponseEntity.status(ec.getHttpStatus().value())
                .body(ErrorResponse.of(ec, ec.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleExplicitValidation(IllegalArgumentException ex, HttpServletRequest req) {
        ErrorCode ec = ErrorCode.VALIDATION_FAILED;
        log.error("[{}] {} {}", ec, req.getMethod(), req.getRequestURI(), ex);
        return ResponseEntity.status(ec.getHttpStatus().value())
                .body(new ErrorResponse(ec.getHttpStatus().value(), ec.name(), ec.getMessage(),
                        req.getRequestURI(), Instant.now(), null));
    }
}

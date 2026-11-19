package live.craftmeet.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Defines the application-specific error codes used to represent failure
 * conditions.
 * Each error code identifies a distinct error scenario and may be used to
 * convey an
 * appropriate HTTP status and human-readable message to clients.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "Validation failed"),
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found"),
	DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "Resource already exists"),
	CONFLICT(HttpStatus.CONFLICT, "Resource is in a conflicting state"),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Authentication required"),
	FORBIDDEN(HttpStatus.FORBIDDEN, "Access denied"),
	INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error"),
	TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "Too many requests");

	private final HttpStatus httpStatus;
	private final String message;

}

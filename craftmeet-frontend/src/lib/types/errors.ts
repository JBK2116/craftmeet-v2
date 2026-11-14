/** A single field-level validation error from the backend. */
export type FieldError = {
    field: string;
    message: string;
};

/** An error returned by the backend API (maps to the Spring `ErrorResponse`). */
export class ApiError extends Error {
    readonly status: number;
    readonly code: string;
    readonly fieldErrors: FieldError[];

    constructor(status: number, code: string, message: string, fieldErrors: FieldError[] = []) {
        super(message);
        this.name = 'ApiError';
        this.status = status;
        this.code = code;
        this.fieldErrors = fieldErrors;
    }
}

/** Authentication/session failure (e.g. token refresh failed). */
export class AuthError extends Error {
    constructor(message = 'Authentication failed') {
        super(message);
        this.name = 'AuthError';
    }
}

/** Rate-limit (429) response from the backend. */
export class RateLimitedError extends Error {
    constructor(message = "You're being rate limited. Please slow down and try again shortly.") {
        super(message);
        this.name = 'RateLimitedError';
    }
}

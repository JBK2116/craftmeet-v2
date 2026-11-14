import { API_BASE_URL } from "@/lib/constants";
import {
  ApiError,
  AuthError,
  RateLimitedError,
  type FieldError,
} from "@/lib/types/errors";

/** Shape of the backend `ErrorResponse` body. */
type ErrorBody = {
  status?: number;
  code?: string;
  message?: string;
  fieldErrors?: FieldError[];
};

/**
 * Refreshes the access/refresh token cookies via `POST /refresh`.
 * Returns whether the refresh succeeded. Uses a raw fetch (not `apiFetch`)
 * to avoid recursion.
 */
export async function refreshTokens(): Promise<boolean> {
  try {
    const res = await fetch(`${API_BASE_URL}/refresh`, {
      method: "POST",
      credentials: "include",
      headers: { "Content-Type": "application/json" },
    });
    return res.ok;
  } catch {
    return false;
  }
}

/**
 * Authenticated fetch. Sends cookies on every request. On a 401, refreshes the
 * tokens and retries exactly once; if the retry 401s again, throws `AuthError`.
 * Throws `RateLimitedError` on 429. Otherwise returns the response.
 */
export async function apiFetch(
  path: string,
  init: RequestInit = {},
  options: { refreshOn401?: boolean } = {},
): Promise<Response> {
  const { refreshOn401 = true } = options;
  const opts: RequestInit = { ...init, credentials: "include" };

  let res = await fetch(`${API_BASE_URL}${path}`, opts);

  if (res.status === 401 && refreshOn401) {
    const ok = await refreshTokens();
    if (!ok) throw new AuthError();
    res = await fetch(`${API_BASE_URL}${path}`, opts);
    if (res.status === 401) throw new AuthError();
  }

  if (res.status === 429) throw new RateLimitedError();

  return res;
}

/**
 * Builds an `ApiError` from a non-OK response by parsing the backend
 * `ErrorResponse` body (falls back to the HTTP status when parsing fails).
 */
export async function apiError(res: Response): Promise<ApiError> {
  let body: ErrorBody = {};
  try {
    body = (await res.json()) as ErrorBody;
  } catch {
    // non-JSON error body; fall through to defaults
  }
  return new ApiError(
    res.status,
    body.code ?? "UNKNOWN",
    body.message ?? `Request failed with status ${res.status}`,
    body.fieldErrors ?? [],
  );
}

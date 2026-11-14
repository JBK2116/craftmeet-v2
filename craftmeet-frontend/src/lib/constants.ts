/**
 * Base URL for API requests. Defaults to same-origin; override with
 * `NEXT_PUBLIC_API_BASE_URL` (e.g. `http://localhost:8080` in local dev) when
 * the backend runs on a different origin.
 */
export const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? '';

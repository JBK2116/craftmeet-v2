/** A user as returned by the backend `GET /me` and auth endpoints. */
export type User = {
    id: string;
    username: string | null;
    email: string;
    verified: boolean;
    verified_at: string | null;
};

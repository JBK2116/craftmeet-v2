import { apiError, apiFetch } from "@/lib/api/client";
import type { User } from "@/lib/types/user";

export type SignupPayload = {
  username: string;
  password: string;
  email: string;
};

export type ResetPasswordPayload = {
  newPassword: string;
};

const JSON_HEADERS = { "Content-Type": "application/json" };

export async function login(email: string, password: string): Promise<User> {
  const res = await apiFetch("/login", {
    method: "POST",
    headers: JSON_HEADERS,
    body: JSON.stringify({ email, password }),
  });
  if (!res.ok) throw await apiError(res);
  return (await res.json()) as User;
}

export async function signup(payload: SignupPayload): Promise<void> {
  const res = await apiFetch("/signup", {
    method: "POST",
    headers: JSON_HEADERS,
    body: JSON.stringify(payload),
  });
  if (!res.ok) throw await apiError(res);
}

export async function verifyEmail(token: string): Promise<void> {
  const res = await apiFetch(`/verify?token=${encodeURIComponent(token)}`);
  if (!res.ok) throw await apiError(res);
}

export async function sendVerificationEmail(email: string): Promise<void> {
  const res = await apiFetch("/verify", {
    method: "POST",
    headers: JSON_HEADERS,
    body: JSON.stringify({ email }),
  });
  if (!res.ok) throw await apiError(res);
}

export async function forgotPassword(email: string): Promise<void> {
  const res = await apiFetch("/forgot-password", {
    method: "POST",
    headers: JSON_HEADERS,
    body: JSON.stringify({ email }),
  });
  if (!res.ok) throw await apiError(res);
}

export async function resetPassword(
  token: string,
  payload: ResetPasswordPayload,
): Promise<void> {
  const res = await apiFetch(
    `/reset-password?token=${encodeURIComponent(token)}`,
    {
      method: "POST",
      headers: JSON_HEADERS,
      body: JSON.stringify(payload),
    },
  );
  if (!res.ok) throw await apiError(res);
}

export async function logout(): Promise<void> {
  const res = await apiFetch("/logout", { method: "POST" });
  if (!res.ok) throw await apiError(res);
}

export async function me(): Promise<User> {
  const res = await apiFetch("/me");
  if (!res.ok) throw await apiError(res);
  return (await res.json()) as User;
}

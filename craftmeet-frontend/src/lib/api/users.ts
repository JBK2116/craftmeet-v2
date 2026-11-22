import { apiError, apiFetch } from "@/lib/api/client";
import type { User } from "@/lib/types/user";

export async function updateUsername(
  id: string,
  newUsername: string,
): Promise<User> {
  const res = await apiFetch("/users/" + id, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ newUsername }),
  });
  if (!res.ok) throw await apiError(res);
  return (await res.json()) as User;
}

export async function deleteUser(id: string): Promise<void> {
  const res = await apiFetch("/users/" + id, { method: "DELETE" });
  if (!res.ok) throw await apiError(res);
}

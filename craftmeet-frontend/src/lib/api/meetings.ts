import { apiError, apiFetch } from "@/lib/api/client";
import type { Meeting } from "@/lib/types/meeting";

export async function getMeetings(): Promise<Meeting[]> {
  const res = await apiFetch("/meetings");
  if (!res.ok) throw await apiError(res);
  return (await res.json()) as Meeting[];
}

export async function deleteMeeting(id: string): Promise<void> {
  const res = await apiFetch(`/meetings/${id}`, { method: "DELETE" });
  if (!res.ok) throw await apiError(res);
}

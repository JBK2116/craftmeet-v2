import type { Meeting, MeetingStatus } from "@/lib/types/meeting";

/** Derives a meeting's status from its session timestamps. The backend stores
 *  no status column — status is computed from `startTime`/`endTime`, not
 *  persisted. */
export function deriveStatus(
  meeting: Pick<Meeting, "startTime" | "endTime">,
): MeetingStatus {
  if (meeting.endTime) return "ended";
  if (meeting.startTime) return "live";
  return "draft";
}

export type MeetingStats = {
  total: number;
  live: number;
  responses: number;
  participants: number;
};

/** Aggregates the headline numbers shown in the dashboard stats bar. */
export function computeMeetingStats(meetings: Meeting[]): MeetingStats {
  return meetings.reduce<MeetingStats>(
    (acc, meeting) => ({
      total: acc.total + 1,
      live: acc.live + (deriveStatus(meeting) === "live" ? 1 : 0),
      responses: acc.responses + meeting.statResponse.totalResponsesReceived,
      participants: acc.participants + meeting.statResponse.totalParticipants,
    }),
    { total: 0, live: 0, responses: 0, participants: 0 },
  );
}

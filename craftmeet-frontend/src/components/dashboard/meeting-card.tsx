"use client";

import { formatRelativeTime } from "@/lib/format";
import { deriveStatus } from "@/lib/meeting";
import type { Meeting } from "@/lib/types/meeting";
import { MeetingActionsMenu } from "./meeting-actions-menu";
import { MeetingStatusBadge } from "./meeting-status-badge";

/** Full-width meeting card used on mobile (replaces the horizontally
 *  scrolling table below the `md` breakpoint). */
export function MeetingCard({
  meeting,
  onDelete,
}: {
  meeting: Meeting;
  onDelete: () => void;
}) {
  const status = deriveStatus(meeting);

  return (
    <div className="rounded-xl border border-border bg-card p-4">
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0">
          <div className="truncate font-medium text-foreground">{meeting.title}</div>
          <div className="mt-0.5 font-mono text-xs tracking-widest text-muted-foreground">
            {meeting.roomCode}
          </div>
        </div>
        <div className="flex shrink-0 items-center gap-1">
          <MeetingStatusBadge status={status} />
          <MeetingActionsMenu meeting={meeting} onDelete={onDelete} />
        </div>
      </div>

      <div className="mt-3 flex flex-wrap items-center gap-x-4 gap-y-1 border-t border-border pt-3 text-xs text-muted-foreground">
        <span>{meeting.statResponse.totalParticipants} participants</span>
        <span>{meeting.statResponse.totalResponsesReceived} responses</span>
        <span>{formatRelativeTime(meeting.createdAt)}</span>
      </div>
    </div>
  );
}

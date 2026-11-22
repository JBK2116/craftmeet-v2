"use client";

import { formatRelativeTime } from "@/lib/format";
import { deriveStatus } from "@/lib/meeting";
import type { Meeting } from "@/lib/types/meeting";
import { MeetingActionsMenu } from "./meeting-actions-menu";
import { MeetingStatusBadge } from "./meeting-status-badge";

export function MeetingDataRow({
  meeting,
  onDelete,
}: {
  meeting: Meeting;
  onDelete: () => void;
}) {
  const status = deriveStatus(meeting);

  return (
    <tr className="border-b border-border transition-colors last:border-0 hover:bg-muted/40">
      <td className="px-4 py-3">
        <div className="font-medium text-foreground">{meeting.title}</div>
        <div className="font-mono text-xs tracking-widest text-muted-foreground">
          {meeting.roomCode}
        </div>
      </td>
      <td className="px-4 py-3">
        <MeetingStatusBadge status={status} />
      </td>
      <td className="px-4 py-3 text-right text-muted-foreground">
        {meeting.statResponse.totalParticipants}
      </td>
      <td className="px-4 py-3 text-right text-muted-foreground">
        {meeting.statResponse.totalResponsesReceived}
      </td>
      <td className="px-4 py-3 text-muted-foreground">
        {formatRelativeTime(meeting.createdAt)}
      </td>
      <td className="px-4 py-3 text-right">
        <MeetingActionsMenu meeting={meeting} onDelete={onDelete} />
      </td>
    </tr>
  );
}

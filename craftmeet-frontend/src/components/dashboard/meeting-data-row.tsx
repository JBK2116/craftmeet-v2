"use client";

import { useRouter } from "next/navigation";
import { MoreHorizontal } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Checkbox } from "@/components/ui/checkbox";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { comingSoon } from "@/lib/coming-soon";
import { formatRelativeTime } from "@/lib/format";
import { deriveStatus } from "@/lib/meeting";
import type { Meeting } from "@/lib/types/meeting";
import { MeetingStatusBadge } from "./meeting-status-badge";

export function MeetingDataRow({
  meeting,
  onDelete,
}: {
  meeting: Meeting;
  onDelete: () => void;
}) {
  const router = useRouter();
  const status = deriveStatus(meeting);

  return (
    <tr className="border-b border-border transition-colors last:border-0 hover:bg-muted/40">
      <td className="px-3 py-3">
        <Checkbox aria-label={`Select ${meeting.title}`} />
      </td>
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
        <DropdownMenu>
          <DropdownMenuTrigger
            render={
              <Button
                variant="ghost"
                size="icon-sm"
                aria-label={`Actions for ${meeting.title}`}
              />
            }
          >
            <MoreHorizontal />
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            {status === "draft" ? (
              <DropdownMenuItem
                onClick={() => router.push(`/meetings/edit?id=${meeting.id}`)}
              >
                Edit
              </DropdownMenuItem>
            ) : (
              <DropdownMenuItem onClick={() => comingSoon("Summaries")}>
                View summary
              </DropdownMenuItem>
            )}
            <DropdownMenuSeparator />
            <DropdownMenuItem variant="destructive" onClick={onDelete}>
              Delete
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </td>
    </tr>
  );
}

"use client";

import { useRouter } from "next/navigation";
import { MoreHorizontal } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { comingSoon } from "@/lib/coming-soon";
import { deriveStatus } from "@/lib/meeting";
import type { Meeting } from "@/lib/types/meeting";

/** ⋮ actions menu shared by the desktop table row and the mobile card. */
export function MeetingActionsMenu({
  meeting,
  onDelete,
}: {
  meeting: Meeting;
  onDelete: () => void;
}) {
  const router = useRouter();
  const status = deriveStatus(meeting);

  return (
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
  );
}

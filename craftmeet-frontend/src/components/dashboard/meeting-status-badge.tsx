"use client";

import { Badge } from "@/components/ui/badge";
import type { MeetingStatus } from "@/lib/types/meeting";

const STATUS: Record<
  MeetingStatus,
  { label: string; variant: "default" | "secondary" | "outline" }
> = {
  live: { label: "Live", variant: "default" },
  draft: { label: "Draft", variant: "secondary" },
  ended: { label: "Ended", variant: "outline" },
};

export function MeetingStatusBadge({ status }: { status: MeetingStatus }) {
  const { label, variant } = STATUS[status];
  return (
    <Badge variant={variant} className={status === "live" ? "gap-1.5" : undefined}>
      {status === "live" && (
        <span aria-hidden className="size-1.5 animate-pulse rounded-full bg-current" />
      )}
      {label}
    </Badge>
  );
}

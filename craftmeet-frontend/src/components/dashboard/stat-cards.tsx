"use client";

import { cn } from "cn";
import { deriveStatus } from "@/lib/meeting";
import type { Meeting } from "@/lib/types/meeting";

/** Live meetings are promoted out of the table, so only draft/ended are filterable. */
export type StatusFilter = "draft" | "ended" | "all";

const CARDS: Array<{ key: StatusFilter; label: string }> = [
  { key: "all", label: "All" },
  { key: "draft", label: "Draft" },
  { key: "ended", label: "Ended" },
];

export function StatCards({
  meetings,
  active,
  onSelect,
}: {
  meetings: Meeting[];
  active: StatusFilter;
  onSelect: (status: StatusFilter) => void;
}) {
  const counts: Record<StatusFilter, number> = {
    all: meetings.length,
    draft: 0,
    ended: 0,
  };
  for (const meeting of meetings) {
    const status = deriveStatus(meeting);
    if (status === "draft" || status === "ended") counts[status] += 1;
  }

  return (
    <div className="grid grid-cols-3 gap-3">
      {CARDS.map(({ key, label }) => {
        const isActive = active === key;
        return (
          <button
            key={key}
            type="button"
            onClick={() => onSelect(key)}
            aria-pressed={isActive}
            className={cn(
              "rounded-lg border px-4 py-3 text-left transition-colors",
              isActive
                ? "border-primary ring-2 ring-primary/15"
                : "border-border hover:bg-muted/40",
            )}
          >
            <div
              className={cn(
                "text-xs",
                isActive ? "font-medium text-primary" : "text-muted-foreground",
              )}
            >
              {label}
            </div>
            <div
              className={cn(
                "mt-1 text-xl font-semibold tracking-tight",
                isActive ? "text-primary" : "text-foreground",
              )}
            >
              {counts[key]}
            </div>
          </button>
        );
      })}
    </div>
  );
}

"use client";

import { useMemo, useState } from "react";
import { ArrowDown, ArrowUp, ArrowUpDown } from "lucide-react";
import { toast } from "sonner";
import { cn } from "cn";
import { Checkbox } from "@/components/ui/checkbox";
import { useDeleteMeeting } from "@/lib/hooks/use-meetings";
import type { Meeting } from "@/lib/types/meeting";
import { DeleteMeetingDialog } from "./delete-meeting-dialog";
import { MeetingDataRow } from "./meeting-data-row";

type SortKey = "title" | "createdAt" | "participants" | "responses";
type SortDir = "asc" | "desc";

function SortHeader({
  label,
  sortKey,
  sort,
  align,
  onSort,
}: {
  label: string;
  sortKey: SortKey;
  sort: { key: SortKey; dir: SortDir };
  align?: "right";
  onSort: (key: SortKey) => void;
}) {
  const active = sort.key === sortKey;
  const Icon = active ? (sort.dir === "asc" ? ArrowUp : ArrowDown) : ArrowUpDown;
  return (
    <th
      className={cn(
        "px-4 py-3 text-xs font-medium text-muted-foreground",
        align === "right" ? "text-right" : "text-left",
      )}
    >
      <button
        type="button"
        onClick={() => onSort(sortKey)}
        className={cn(
          "inline-flex items-center gap-1 transition-colors hover:text-foreground",
          active && "text-foreground",
        )}
      >
        {label}
        <Icon className={cn("size-3.5", !active && "opacity-50")} />
      </button>
    </th>
  );
}

export function MeetingsDataTable({ meetings }: { meetings: Meeting[] }) {
  const deleteMeeting = useDeleteMeeting();
  const [pending, setPending] = useState<Meeting | null>(null);
  const [sort, setSort] = useState<{ key: SortKey; dir: SortDir }>({
    key: "createdAt",
    dir: "desc",
  });

  const sorted = useMemo(() => {
    const { key, dir } = sort;
    const sign = dir === "asc" ? 1 : -1;
    return [...meetings].sort((a, b) => {
      const cmp =
        key === "title"
          ? a.title.localeCompare(b.title)
          : key === "createdAt"
            ? new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
            : key === "participants"
              ? a.statResponse.totalParticipants - b.statResponse.totalParticipants
              : a.statResponse.totalResponsesReceived - b.statResponse.totalResponsesReceived;
      return cmp * sign;
    });
  }, [meetings, sort]);

  function toggleSort(key: SortKey) {
    setSort((s) =>
      s.key === key ? { key, dir: s.dir === "asc" ? "desc" : "asc" } : { key, dir: "asc" },
    );
  }

  function confirmDelete() {
    if (!pending) return;
    deleteMeeting.mutate(pending.id, {
      onSuccess: () => {
        toast.success(`Deleted "${pending.title}"`);
        setPending(null);
      },
      onError: () => {
        toast.error("Couldn't delete the meeting. Please try again.");
      },
    });
  }

  return (
    <>
      <div className="overflow-hidden rounded-xl border border-border bg-card">
        <div className="overflow-x-auto">
          <table className="w-full min-w-[720px] text-sm">
            <thead>
              <tr className="border-b border-border bg-muted/50">
                <th className="w-10 px-3 py-3">
                  <Checkbox aria-label="Select all" />
                </th>
                <SortHeader label="Meeting" sortKey="title" sort={sort} onSort={toggleSort} />
                <th className="px-4 py-3 text-left text-xs font-medium text-muted-foreground">
                  Status
                </th>
                <SortHeader
                  label="Participants"
                  sortKey="participants"
                  align="right"
                  sort={sort}
                  onSort={toggleSort}
                />
                <SortHeader
                  label="Responses"
                  sortKey="responses"
                  align="right"
                  sort={sort}
                  onSort={toggleSort}
                />
                <SortHeader label="Created" sortKey="createdAt" sort={sort} onSort={toggleSort} />
                <th className="px-4 py-3 text-right">
                  <span className="sr-only">Actions</span>
                </th>
              </tr>
            </thead>
            <tbody>
              {sorted.map((meeting) => (
                <MeetingDataRow
                  key={meeting.id}
                  meeting={meeting}
                  onDelete={() => setPending(meeting)}
                />
              ))}
            </tbody>
          </table>
        </div>
        <div className="border-t border-border px-4 py-2.5 text-xs text-muted-foreground">
          {meetings.length} result{meetings.length === 1 ? "" : "s"}
        </div>
      </div>

      <DeleteMeetingDialog
        meeting={pending}
        isDeleting={deleteMeeting.isPending}
        onConfirm={confirmDelete}
        onCancel={() => setPending(null)}
      />
    </>
  );
}

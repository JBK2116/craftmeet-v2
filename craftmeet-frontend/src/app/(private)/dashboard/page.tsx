"use client";

import { useState } from "react";
import { CreateMeetingButton } from "@/components/dashboard/create-meeting-button";
import { EmptyState } from "@/components/dashboard/empty-state";
import { LiveMeetingCard } from "@/components/dashboard/live-meeting-card";
import { MeetingsDataTable } from "@/components/dashboard/meetings-data-table";
import { MeetingsSkeleton } from "@/components/dashboard/meetings-skeleton";
import { StatCards, type StatusFilter } from "@/components/dashboard/stat-cards";
import { useMeetings } from "@/lib/hooks/use-meetings";
import { deriveStatus } from "@/lib/meeting";
import { useSearch } from "@/lib/search/search-provider";

export default function DashboardPage() {
  const [statusFilter, setStatusFilter] = useState<StatusFilter>("all");
  const { query } = useSearch();
  const meetingsQuery = useMeetings();

  const meetings = meetingsQuery.data ?? [];
  // Live meetings are promoted into the rejoin card, not shown in the table.
  const live = meetings.find((meeting) => deriveStatus(meeting) === "live") ?? null;
  const nonLive = live ? meetings.filter((meeting) => meeting.id !== live.id) : meetings;

  const searchTerm = query.trim().toLowerCase();
  const filtered = nonLive.filter((meeting) => {
    const matchesStatus =
      statusFilter === "all" || deriveStatus(meeting) === statusFilter;
    const matchesSearch =
      searchTerm === "" ||
      meeting.title.toLowerCase().includes(searchTerm) ||
      meeting.roomCode.includes(searchTerm);
    return matchesStatus && matchesSearch;
  });

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <h1 className="text-2xl font-bold tracking-tight">Meetings</h1>
        <CreateMeetingButton />
      </div>

      {meetingsQuery.isPending ? (
        <MeetingsSkeleton />
      ) : meetings.length === 0 ? (
        <EmptyState />
      ) : (
        <>
          {live && <LiveMeetingCard meeting={live} />}
          {nonLive.length > 0 && (
            <>
              <StatCards meetings={nonLive} active={statusFilter} onSelect={setStatusFilter} />
              <MeetingsDataTable meetings={filtered} />
            </>
          )}
        </>
      )}
    </div>
  );
}

"use client";

import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { comingSoon } from "@/lib/coming-soon";
import { formatRelativeTime } from "@/lib/format";
import type { Meeting } from "@/lib/types/meeting";
import { MeetingStatusBadge } from "./meeting-status-badge";

export function LiveMeetingCard({ meeting }: { meeting: Meeting }) {
  return (
    <Card className="border-primary/30 bg-primary/5">
      <CardContent className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div className="flex flex-col gap-1.5">
          <MeetingStatusBadge status="live" />
          <h2 className="text-xl font-semibold tracking-tight">{meeting.title}</h2>
          <p className="text-sm text-muted-foreground">
            Started {formatRelativeTime(meeting.startTime ?? "")} · Room code{" "}
            <span className="font-mono tracking-widest">{meeting.roomCode}</span>
          </p>
        </div>
        <Button onClick={() => comingSoon("Reconnect")} className="sm:w-auto">
          Reconnect
        </Button>
      </CardContent>
    </Card>
  );
}

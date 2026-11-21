"use client";

import { CalendarPlus } from "lucide-react";
import { CreateMeetingButton } from "./create-meeting-button";

export function EmptyState() {
  return (
    <div className="flex flex-col items-center justify-center rounded-2xl border border-dashed border-border px-6 py-20 text-center">
      <div className="mb-4 flex size-12 items-center justify-center rounded-full bg-secondary text-secondary-foreground">
        <CalendarPlus className="size-6" />
      </div>
      <h2 className="text-xl font-semibold tracking-tight">
        Create your first meeting
      </h2>
      <p className="mt-2 max-w-sm text-sm text-muted-foreground">
        Build a poll, Q&amp;A, or feedback session in a couple of minutes and
        share the room code.
      </p>
      <CreateMeetingButton className="mt-6 h-10 px-6" />
    </div>
  );
}

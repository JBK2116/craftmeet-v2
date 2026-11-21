"use client";

export function MeetingsSkeleton() {
  return (
    <div className="space-y-3">
      <span className="sr-only">Loading meetings</span>
      {Array.from({ length: 5 }).map((_, i) => (
        <div key={i} className="h-12 animate-pulse rounded-lg bg-muted" />
      ))}
    </div>
  );
}

"use client";

import { BarChart3, MessageSquare, Users } from "lucide-react";
import { Button } from "@/components/ui/button";
import type { StatLifeTime } from "@/lib/types/meeting";

const STATS = [
  { key: "totalParticipants", label: "Total participants", icon: Users },
  { key: "totalQuestionsAsked", label: "Questions asked", icon: MessageSquare },
  { key: "totalResponsesReceived", label: "Responses received", icon: BarChart3 },
] as const;

export function LifetimeStats({
  stats,
  isLoading,
  isError,
  onRetry,
}: {
  stats?: StatLifeTime;
  isLoading: boolean;
  isError: boolean;
  onRetry: () => void;
}) {
  if (isLoading) {
    return (
      <div className="grid grid-cols-1 gap-3 sm:grid-cols-3">
        {STATS.map(({ key, label }) => (
          <div
            key={key}
            className="rounded-xl bg-card p-4 ring-1 ring-foreground/10"
          >
            <div className="text-xs text-muted-foreground">{label}</div>
            <div className="mt-2 h-8 w-16 animate-pulse rounded-md bg-muted" />
          </div>
        ))}
      </div>
    );
  }

  if (isError) {
    return (
      <div className="flex items-center justify-between gap-3 rounded-xl border border-destructive/40 bg-destructive/10 p-4 text-sm text-destructive">
        <span>{"Couldn't load your lifetime stats."}</span>
        <Button variant="outline" size="sm" onClick={onRetry}>
          Retry
        </Button>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 gap-3 sm:grid-cols-3">
      {STATS.map(({ key, label, icon: Icon }) => (
        <div
          key={key}
          className="rounded-xl bg-card p-4 ring-1 ring-foreground/10"
        >
          <div className="flex items-center gap-2 text-xs text-muted-foreground">
            <Icon className="size-4" />
            {label}
          </div>
          <div className="mt-1 text-2xl font-semibold tracking-tight text-foreground">
            {stats?.[key] ?? 0}
          </div>
        </div>
      ))}
    </div>
  );
}

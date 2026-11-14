"use client";

import { useState } from "react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";

export function JoinMeeting() {
  const [code, setCode] = useState("");

  function handleJoin() {
    // Meetings backend is not built yet — stub the action for now.
    toast.info("Joining meetings is coming soon.");
  }

  return (
    <section id="join-meeting" className="border-t border-border px-6 py-24">
      <div className="mx-auto max-w-2xl text-center">
        <h2 className="text-3xl font-bold tracking-tight text-foreground sm:text-4xl">
          Have a room code?
        </h2>
        <p className="mt-4 text-sm leading-relaxed text-muted-foreground">
          Jump straight into an active session. No account needed.
        </p>
        <div className="mt-8 flex flex-col items-center gap-3">
          <input
            type="text"
            inputMode="numeric"
            placeholder="Room code"
            maxLength={8}
            value={code}
            onChange={(e) =>
              setCode(e.target.value.replace(/\D/g, "").slice(0, 8))
            }
            className="h-10 w-full max-w-xs rounded-full border border-border bg-card px-5 text-sm text-foreground outline-none transition placeholder:text-muted-foreground focus:border-primary/40 focus:ring-2 focus:ring-primary/20 sm:max-w-md"
          />
          <Button
            onClick={handleJoin}
            className="h-10 w-full max-w-xs rounded-full px-6 text-sm font-medium transition-colors sm:max-w-md"
          >
            Join now
          </Button>
        </div>
      </div>
    </section>
  );
}

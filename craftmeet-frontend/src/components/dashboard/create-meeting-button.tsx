"use client";

import { Plus } from "lucide-react";
import { toast } from "sonner";
import { cn } from "cn";
import { Button } from "@/components/ui/button";

function handleCreate() {
  // The create-meeting flow is a separate page; not built yet.
  toast.info("Creating meetings is coming soon.");
}

export function CreateMeetingButton({ className }: { className?: string }) {
  return (
    <Button onClick={handleCreate} className={className}>
      <Plus />
      Create meeting
    </Button>
  );
}

export function CreateMeetingIconButton({ className }: { className?: string }) {
  return (
    <Button
      onClick={handleCreate}
      size="icon"
      aria-label="Create meeting"
      className={cn("rounded-full", className)}
    >
      <Plus />
    </Button>
  );
}

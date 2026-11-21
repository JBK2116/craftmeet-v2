"use client";

import Link from "next/link";
import { CircleHelp } from "lucide-react";
import { Button } from "@/components/ui/button";

export function HelpCard() {
  return (
    <div className="rounded-xl border border-border bg-card p-4">
      <div className="flex items-center gap-2">
        <CircleHelp className="size-4 text-muted-foreground" />
        <p className="text-sm font-medium">Need help?</p>
      </div>
      <p className="mt-1 text-xs text-muted-foreground">
        Questions about creating a meeting? We&apos;re here for you.
      </p>
      <Button
        variant="link"
        size="sm"
        className="mt-2 h-auto p-0"
        render={<Link href="/contact" />}
        nativeButton={false}
      >
        Contact support
      </Button>
    </div>
  );
}

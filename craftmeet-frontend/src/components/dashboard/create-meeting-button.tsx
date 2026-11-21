"use client";

import Link from "next/link";
import { Plus } from "lucide-react";
import { Button } from "@/components/ui/button";

export function CreateMeetingButton({ className }: { className?: string }) {
  return (
    <Button
      render={<Link href="/meetings/create" />}
      nativeButton={false}
      className={className}
    >
      <Plus />
      Create meeting
    </Button>
  );
}

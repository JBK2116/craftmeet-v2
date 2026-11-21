import { Suspense } from "react";
import { EditMeetingForm } from "@/components/meetings/create/edit-meeting-form";

export default function EditMeetingPage() {
  return (
    <Suspense
      fallback={
        <div className="flex min-h-screen items-center justify-center bg-background">
          <div className="h-8 w-8 rounded-full border-2 border-muted border-t-primary animate-spin" />
        </div>
      }
    >
      <EditMeetingForm />
    </Suspense>
  );
}

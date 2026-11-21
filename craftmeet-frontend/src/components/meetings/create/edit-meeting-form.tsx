"use client";

import {useRouter, useSearchParams} from "next/navigation";
import {ArrowLeft} from "lucide-react";
import {toast} from "sonner";
import {Button} from "@/components/ui/button";
import {useMeeting, useUpdateMeeting} from "@/lib/hooks/use-meetings";
import {deriveStatus} from "@/lib/meeting";
import {
  toMeetingFormValues,
  toMeetingRequest,
  type MeetingFormValues,
} from "@/lib/schemas/meeting";
import {MeetingForm} from "./meeting-form";

/** The edit-meeting wizard: the create form prefilled with a draft meeting's
 *  current state, submitted via `PUT /meetings/{id}`. Only drafts are editable. */
export function EditMeetingForm() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const meetingId = searchParams.get("id") ?? "";

  const {data: meeting, isLoading, isError} = useMeeting(meetingId);
  const updateMeeting = useUpdateMeeting();

  async function handleSubmit(values: MeetingFormValues) {
    await updateMeeting.mutateAsync({
      id: meetingId,
      body: toMeetingRequest(values),
    });
    toast.success("Meeting updated");
    router.push("/dashboard");
  }

  if (!meetingId) {
    return (
      <Unavailable
        title="Meeting not found"
        message="This link is missing a meeting id. Return to your dashboard and open a draft to edit it."
      />
    );
  }

  if (isLoading) {
    return <EditSkeleton/>;
  }

  if (isError || !meeting) {
    return (
      <Unavailable
        title="Meeting not found"
        message="We couldn't find this meeting. It may have been deleted."
      />
    );
  }

  if (deriveStatus(meeting) !== "draft") {
    return (
      <Unavailable
        title="This meeting can't be edited"
        message="Only draft meetings can be edited. This meeting has already started or ended."
      />
    );
  }

  return (
    <MeetingForm
      key={meeting.id}
      initialValues={toMeetingFormValues(meeting)}
      title="Edit meeting"
      subtitle="Update your meeting and its questions."
      submitLabel="Save changes"
      discardMessage="Your changes will be lost if you leave this page."
      isSubmitting={updateMeeting.isPending}
      onSubmit={handleSubmit}
    />
  );
}

function Unavailable({title, message}: {title: string; message: string}) {
  const router = useRouter();
  return (
    <div className="space-y-6">
      <header className="flex items-center gap-3">
        <Button
          type="button"
          variant="ghost"
          size="icon"
          className="rounded-full"
          onClick={() => router.push("/dashboard")}
          aria-label="Back to dashboard"
        >
          <ArrowLeft/>
        </Button>
        <h1 className="text-2xl font-bold tracking-tight">{title}</h1>
      </header>
      <div className="rounded-lg border border-border bg-card px-6 py-16 text-center">
        <p className="mx-auto max-w-sm text-sm text-muted-foreground">{message}</p>
        <Button
          type="button"
          className="mt-6"
          onClick={() => router.push("/dashboard")}
        >
          Back to dashboard
        </Button>
      </div>
    </div>
  );
}

function EditSkeleton() {
  return (
    <div className="space-y-6" aria-busy="true">
      <span className="sr-only">Loading meeting</span>
      <header className="flex items-center gap-3">
        <div className="size-10 animate-pulse rounded-full bg-muted"/>
        <div className="space-y-2">
          <div className="h-6 w-44 animate-pulse rounded bg-muted"/>
          <div className="h-4 w-72 animate-pulse rounded bg-muted"/>
        </div>
      </header>
      <div className="h-[28rem] animate-pulse rounded-lg bg-muted"/>
    </div>
  );
}

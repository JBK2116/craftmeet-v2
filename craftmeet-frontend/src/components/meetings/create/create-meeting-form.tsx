"use client";

import {useRouter} from "next/navigation";
import {toast} from "sonner";
import {useCreateMeeting} from "@/lib/hooks/use-meetings";
import {
  DEFAULT_MEETING_VALUES,
  toMeetingRequest,
  type MeetingFormValues,
} from "@/lib/schemas/meeting";
import {MeetingForm} from "./meeting-form";

export function CreateMeetingForm() {
  const router = useRouter();
  const createMeeting = useCreateMeeting();

  async function handleSubmit(values: MeetingFormValues) {
    await createMeeting.mutateAsync(toMeetingRequest(values));
    toast.success("Meeting created");
    router.push("/dashboard");
  }

  return (
    <MeetingForm
      initialValues={DEFAULT_MEETING_VALUES}
      title="Create meeting"
      subtitle="Set up your meeting and its questions."
      submitLabel="Create meeting"
      discardMessage="Your meeting draft will be lost if you leave this page."
      isSubmitting={createMeeting.isPending}
      onSubmit={handleSubmit}
    />
  );
}

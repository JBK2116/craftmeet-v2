import { MEETING_LIMITS } from "@/lib/meeting-constants";
import { TextField } from "./text-field";
import { TextareaField } from "./textarea-field";

export function StepBasicInfo() {
  return (
    <div className="space-y-6">
      <TextField
        name="title"
        label="Title"
        required
        maxLength={MEETING_LIMITS.title.max}
        placeholder="e.g. Q3 Team Retrospective"
      />
      <TextareaField
        name="description"
        label="Description"
        maxLength={MEETING_LIMITS.description.max}
        hint="Optional context shown to participants"
      />
    </div>
  );
}

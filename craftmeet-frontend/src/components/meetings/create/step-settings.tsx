import { MEETING_LIMITS } from "@/lib/meeting-constants";
import { NumberField } from "./number-field";

export function StepSettings() {
  return (
    <div className="space-y-6">
      <NumberField
        name="duration"
        label="Duration (minutes)"
        min={MEETING_LIMITS.duration.min}
        max={MEETING_LIMITS.duration.max}
      />
      <NumberField
        name="participantCap"
        label="Participant cap"
        min={MEETING_LIMITS.participantCap.min}
        max={MEETING_LIMITS.participantCap.max}
      />
    </div>
  );
}

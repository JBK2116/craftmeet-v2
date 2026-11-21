"use client";

import { MEETING_LIMITS } from "@/lib/meeting-constants";
import { NumberField } from "../number-field";

export function RatingScaleConfig({ index }: { index: number }) {
  return (
    <div className="grid grid-cols-2 gap-4">
      <NumberField
        name={`questions.${index}.min`}
        label="Minimum"
        min={MEETING_LIMITS.rating.min}
        max={MEETING_LIMITS.rating.max}
      />
      <NumberField
        name={`questions.${index}.max`}
        label="Maximum"
        min={MEETING_LIMITS.rating.min}
        max={MEETING_LIMITS.rating.max}
        hint="Must be greater than the minimum"
      />
    </div>
  );
}

"use client";

import { MEETING_LIMITS } from "@/lib/meeting-constants";
import { NumberField } from "../number-field";

export function LongAnswerConfig({ index }: { index: number }) {
  return (
    <NumberField
      name={`questions.${index}.maxLength`}
      label="Maximum length (characters)"
      min={MEETING_LIMITS.maxLength.min}
      max={MEETING_LIMITS.maxLength.max}
      hint="The longest response participants can submit"
    />
  );
}

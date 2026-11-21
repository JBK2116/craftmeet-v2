"use client";

import type { UseFieldArrayReturn } from "react-hook-form";
import { MEETING_LIMITS } from "@/lib/meeting-constants";
import type { MeetingFormValues } from "@/lib/schemas/meeting";
import type { QuestionType } from "@/lib/types/meeting";
import { QuestionCard } from "./question-card";
import { QuestionTypeMenu } from "./question-type-menu";

export function StepQuestions({
  fields,
  onAppend,
  onRemove,
  onMove,
}: {
  fields: UseFieldArrayReturn<MeetingFormValues, "questions">["fields"];
  onAppend: (type: QuestionType) => void;
  onRemove: (index: number) => void;
  onMove: (index: number, direction: -1 | 1) => void;
}) {
  return (
    <div className="space-y-4">
      {fields.length === 0 ? (
        <div className="rounded-xl border border-dashed border-border p-8 text-center text-sm text-muted-foreground">
          No questions yet. Add your first question to get started.
        </div>
      ) : (
        fields.map((field, index) => (
          <QuestionCard
            key={field.id}
            index={index}
            isFirst={index === 0}
            isLast={index === fields.length - 1}
            onRemove={() => onRemove(index)}
            onMove={(dir) => onMove(index, dir)}
          />
        ))
      )}
      <QuestionTypeMenu
        onSelect={onAppend}
        disabled={fields.length >= MEETING_LIMITS.questions.max}
      />
    </div>
  );
}

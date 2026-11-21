"use client";

import { ChevronDown, ChevronUp, Trash2 } from "lucide-react";
import { useFormContext, useWatch } from "react-hook-form";
import { Button } from "@/components/ui/button";
import { MEETING_LIMITS } from "@/lib/meeting-constants";
import type { MeetingFormValues } from "@/lib/schemas/meeting";
import type { QuestionType } from "@/lib/types/meeting";
import { LongAnswerConfig } from "./configs/long-answer-config";
import { MultipleChoiceConfig } from "./configs/multiple-choice-config";
import { RankedVotingConfig } from "./configs/ranked-voting-config";
import { RatingScaleConfig } from "./configs/rating-scale-config";
import { YesNoConfig } from "./configs/yes-no-config";
import { QUESTION_TYPE_META } from "./question-type-meta";
import { TextField } from "./text-field";

function renderConfig(type: QuestionType, index: number) {
  switch (type) {
    case "multiple_choice":
      return <MultipleChoiceConfig index={index} />;
    case "long_answer":
      return <LongAnswerConfig index={index} />;
    case "ranked_voting":
      return <RankedVotingConfig index={index} />;
    case "rating_scale":
      return <RatingScaleConfig index={index} />;
    case "yes_no":
      return <YesNoConfig index={index} />;
  }
}

export function QuestionCard({
  index,
  isFirst,
  isLast,
  onRemove,
  onMove,
}: {
  index: number;
  isFirst: boolean;
  isLast: boolean;
  onRemove: () => void;
  onMove: (dir: -1 | 1) => void;
}) {
  const { control } = useFormContext<MeetingFormValues>();
  const type = useWatch({
    control,
    name: `questions.${index}.type`,
  });

  const meta = QUESTION_TYPE_META[type];
  const Icon = meta.icon;

  return (
    <div className="rounded-xl border border-border bg-card p-4">
      <div className="flex items-center gap-2">
        <Icon className="size-4 text-muted-foreground" />
        <span className="text-sm font-medium">
          Q{index + 1} · {meta.label}
        </span>
        <div className="ml-auto flex items-center gap-1">
          <Button
            type="button"
            variant="ghost"
            size="icon-sm"
            disabled={isFirst}
            onClick={() => onMove(-1)}
            aria-label="Move question up"
          >
            <ChevronUp />
          </Button>
          <Button
            type="button"
            variant="ghost"
            size="icon-sm"
            disabled={isLast}
            onClick={() => onMove(1)}
            aria-label="Move question down"
          >
            <ChevronDown />
          </Button>
          <Button
            type="button"
            variant="ghost"
            size="icon-sm"
            onClick={onRemove}
            aria-label="Remove question"
          >
            <Trash2 />
          </Button>
        </div>
      </div>

      <div className="mt-4">
        <TextField
          name={`questions.${index}.prompt`}
          label="Prompt"
          required
          maxLength={MEETING_LIMITS.prompt.max}
          placeholder="Ask participants something…"
        />
      </div>

      <div className="mt-4">{renderConfig(type, index)}</div>
    </div>
  );
}

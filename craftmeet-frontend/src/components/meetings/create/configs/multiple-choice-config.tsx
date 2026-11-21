"use client";

import { useState } from "react";
import { Controller, useFormContext } from "react-hook-form";
import { Plus, Trash2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Checkbox } from "@/components/ui/checkbox";
import { MEETING_LIMITS } from "@/lib/meeting-constants";
import { TextField } from "../text-field";

const OPTION_FIELDS = ["option1", "option2", "option3", "option4"] as const;

export function MultipleChoiceConfig({ index }: { index: number }) {
  const { control, setValue } = useFormContext();
  const [visibleCount, setVisibleCount] = useState(2);

  const removeLastOption = () => {
    const lastField = OPTION_FIELDS[visibleCount - 1];
    setValue(`questions.${index}.${lastField}`, "");
    setVisibleCount((n) => Math.max(2, n - 1));
  };

  return (
    <div className="space-y-3">
      {OPTION_FIELDS.slice(0, visibleCount).map((key, i) => {
        const required = i < 2;
        const isLast = i === visibleCount - 1;

        return (
          <div key={key} className="flex items-end gap-2">
            <span className="mb-1 flex size-6 shrink-0 items-center justify-center rounded-full bg-muted text-xs font-medium text-muted-foreground">
              {i + 1}
            </span>
            <div className="min-w-0 flex-1">
              <TextField
                name={`questions.${index}.${key}`}
                label={
                  required ? `Option ${i + 1}` : `Option ${i + 1} (optional)`
                }
                required={required}
                maxLength={MEETING_LIMITS.option.max}
                placeholder={`Option ${i + 1}`}
              />
            </div>
            {!required && isLast ? (
              <Button
                type="button"
                variant="ghost"
                size="icon-sm"
                onClick={removeLastOption}
                aria-label="Remove last option"
              >
                <Trash2 />
              </Button>
            ) : null}
          </div>
        );
      })}

      {visibleCount < 4 ? (
        <Button
          type="button"
          variant="outline"
          size="sm"
          onClick={() => setVisibleCount((n) => n + 1)}
        >
          <Plus />
          Add option
        </Button>
      ) : null}

      <Controller
        name={`questions.${index}.allowMultiple`}
        control={control}
        render={({ field }) => (
          <label className="flex items-center gap-2 text-sm">
            <Checkbox
              checked={!!field.value}
              onCheckedChange={(v) => field.onChange(v === true)}
            />
            Allow multiple selections
          </label>
        )}
      />
    </div>
  );
}

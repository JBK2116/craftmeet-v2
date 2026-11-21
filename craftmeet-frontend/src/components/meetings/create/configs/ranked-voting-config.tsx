"use client";

import { useState } from "react";
import { useFormContext } from "react-hook-form";
import { Plus, Trash2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { MEETING_LIMITS } from "@/lib/meeting-constants";
import { TextField } from "../text-field";

const ITEM_FIELDS = ["item1", "item2", "item3", "item4"] as const;

export function RankedVotingConfig({ index }: { index: number }) {
  const { setValue } = useFormContext();
  const [visibleCount, setVisibleCount] = useState(2);

  const removeLastItem = () => {
    const lastField = ITEM_FIELDS[visibleCount - 1];
    setValue(`questions.${index}.${lastField}`, "");
    setVisibleCount((n) => Math.max(2, n - 1));
  };

  return (
    <div className="space-y-3">
      {ITEM_FIELDS.slice(0, visibleCount).map((key, i) => {
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
                label={required ? `Item ${i + 1}` : `Item ${i + 1} (optional)`}
                required={required}
                maxLength={MEETING_LIMITS.option.max}
                placeholder={`Item ${i + 1}`}
              />
            </div>
            {!required && isLast ? (
              <Button
                type="button"
                variant="ghost"
                size="icon-sm"
                onClick={removeLastItem}
                aria-label="Remove last item"
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
          Add item
        </Button>
      ) : null}
    </div>
  );
}

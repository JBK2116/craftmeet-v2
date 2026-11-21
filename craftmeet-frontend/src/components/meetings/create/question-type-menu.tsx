"use client";

import { Plus } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import type { QuestionType } from "@/lib/types/meeting";
import { QUESTION_TYPE_META } from "./question-type-meta";

const QUESTION_TYPES = Object.keys(QUESTION_TYPE_META) as QuestionType[];

export function QuestionTypeMenu({
  onSelect,
  disabled,
}: {
  onSelect: (type: QuestionType) => void;
  disabled?: boolean;
}) {
  return (
    <DropdownMenu>
      <DropdownMenuTrigger
        render={
          <Button
            variant="outline"
            disabled={disabled}
            className="w-full border-dashed"
          />
        }
      >
        <Plus />
        Add question
      </DropdownMenuTrigger>
      <DropdownMenuContent align="start" className="w-72">
        {QUESTION_TYPES.map((type) => {
          const meta = QUESTION_TYPE_META[type];
          const Icon = meta.icon;

          return (
            <DropdownMenuItem
              key={type}
              onClick={() => onSelect(type)}
              className="items-start py-2"
            >
              <Icon className="mt-0.5 size-4" />
              <span className="flex flex-col">
                <span className="text-sm font-medium">{meta.label}</span>
                <span className="text-xs text-muted-foreground">
                  {meta.description}
                </span>
              </span>
            </DropdownMenuItem>
          );
        })}
      </DropdownMenuContent>
    </DropdownMenu>
  );
}

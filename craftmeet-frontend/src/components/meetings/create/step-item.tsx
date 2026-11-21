"use client";

import { Check } from "lucide-react";
import { cn } from "cn";
import type { Step } from "./steps-config";

export function StepItem({
  step,
  index,
  state,
  onClick,
  disabled,
}: {
  step: Step;
  index: number;
  state: "active" | "completed" | "upcoming";
  onClick: () => void;
  disabled?: boolean;
}) {
  return (
    <li className="flex-1 lg:flex-none">
      <button
        type="button"
        onClick={onClick}
        disabled={disabled}
        aria-current={state === "active" ? "step" : undefined}
        className={cn(
          "flex w-full items-center justify-center gap-3 rounded-lg border p-2 text-left transition-colors",
          "focus-visible:ring-3 focus-visible:ring-ring/50 focus-visible:outline-none",
          "lg:justify-start",
          state === "active" && "border-primary bg-primary/10",
          state === "completed" && "border-transparent hover:bg-muted",
          state === "upcoming" && "border-transparent",
          disabled && "cursor-not-allowed opacity-60",
        )}
      >
        <span
          className={cn(
            "flex size-7 shrink-0 items-center justify-center rounded-md border text-sm font-medium",
            state === "active" && "border-primary text-primary",
            state === "completed" &&
              "border-transparent bg-primary text-primary-foreground",
            state === "upcoming" && "border-border text-muted-foreground",
          )}
        >
          {state === "completed" ? <Check className="size-4" /> : index + 1}
        </span>
        <span className="hidden flex-col lg:flex">
          <span
            className={cn(
              "text-sm font-medium",
              state === "active" && "text-primary",
              state === "upcoming" && "text-muted-foreground",
            )}
          >
            {step.title}
          </span>
          <span className="text-xs text-muted-foreground">{step.subtitle}</span>
        </span>
      </button>
    </li>
  );
}

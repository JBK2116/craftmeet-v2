"use client";

import { STEPS } from "./steps-config";
import { StepItem } from "./step-item";
import { HelpCard } from "./help-card";

export function ProgressRail({
  currentIndex,
  completedIndex,
  onStepSelect,
}: {
  currentIndex: number;
  completedIndex: number;
  onStepSelect: (index: number) => void;
}) {
  const fillPercent = ((currentIndex + 1) / STEPS.length) * 100;

  return (
    <aside className="flex flex-col gap-4 lg:gap-6">
      <div
        className="h-1.5 w-full overflow-hidden rounded-full bg-muted"
        role="progressbar"
        aria-valuemin={1}
        aria-valuemax={STEPS.length}
        aria-valuenow={currentIndex + 1}
        aria-label="Meeting setup progress"
      >
        <div
          className="h-full rounded-full bg-primary transition-[width]"
          style={{ width: `${fillPercent}%` }}
        />
      </div>
      <ol className="flex items-center gap-2 lg:flex-col lg:items-stretch lg:gap-1">
        {STEPS.map((step, index) => {
          const state =
            index === currentIndex
              ? "active"
              : index <= completedIndex
                ? "completed"
                : "upcoming";
          return (
            <StepItem
              key={step.id}
              step={step}
              index={index}
              state={state}
              onClick={() => onStepSelect(index)}
              disabled={index > completedIndex && index !== currentIndex}
            />
          );
        })}
      </ol>
      <div className="hidden lg:block">
        <HelpCard />
      </div>
    </aside>
  );
}

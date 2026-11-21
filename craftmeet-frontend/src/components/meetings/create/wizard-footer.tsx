"use client";

import { LoaderCircle } from "lucide-react";
import { Button } from "@/components/ui/button";

export function WizardFooter({
  onBack,
  backHidden,
  onNext,
  nextLabel,
  nextDisabled,
  submitting,
}: {
  onBack: () => void;
  backHidden?: boolean;
  onNext: () => void;
  nextLabel: string;
  nextDisabled?: boolean;
  submitting?: boolean;
}) {
  return (
    <footer className="sticky bottom-0 flex items-center gap-3 border-t border-border bg-card p-6">
      {backHidden ? null : (
        <Button type="button" variant="outline" onClick={onBack}>
          Back
        </Button>
      )}
      <Button
        type="button"
        className="ml-auto"
        onClick={onNext}
        disabled={nextDisabled || submitting}
      >
        {submitting ? <LoaderCircle className="size-4 animate-spin" /> : null}
        {nextLabel}
      </Button>
    </footer>
  );
}

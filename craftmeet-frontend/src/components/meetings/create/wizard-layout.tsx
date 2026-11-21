"use client";

import type { ReactNode } from "react";

/** Two-column shell for the create-meeting wizard. The parent renders the
 *  `<ProgressRail />` first and the form card (`<FormCard />`) second; on
 *  mobile the grid stacks to a single column and the rail collapses to a
 *  horizontal stepper. */
export function WizardLayout({ children }: { children: ReactNode }) {
  return (
    <div className="grid w-full gap-6 lg:grid-cols-[280px_1fr]">
      {children}
    </div>
  );
}

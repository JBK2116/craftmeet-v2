"use client";

import type { ReactNode } from "react";
import { cn } from "cn";

export function FormCard({
  title,
  description,
  footer,
  children,
  className,
}: {
  title: string;
  description: string;
  footer: ReactNode;
  children: ReactNode;
  className?: string;
}) {
  return (
    <section
      className={cn(
        "flex flex-col overflow-hidden rounded-xl border border-border bg-card",
        className,
      )}
    >
      <header className="border-b border-border p-6">
        <h2 className="text-lg font-semibold">{title}</h2>
        <p className="mt-1 text-sm text-muted-foreground">{description}</p>
      </header>
      <div className="flex-1 overflow-y-auto p-6">{children}</div>
      {footer}
    </section>
  );
}

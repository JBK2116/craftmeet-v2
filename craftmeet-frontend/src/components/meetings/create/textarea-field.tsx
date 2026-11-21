"use client";

import { useFormContext } from "react-hook-form";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { getFieldError } from "./field-error";

export function TextareaField({
  name,
  label,
  hint,
  placeholder,
  maxLength,
  rows,
}: {
  name: string;
  label: string;
  hint?: string;
  placeholder?: string;
  maxLength?: number;
  rows?: number;
}) {
  const {
    register,
    formState: { errors },
  } = useFormContext();

  const error = getFieldError(errors, name);
  const hintId = `${name}-hint`;
  const errorId = `${name}-error`;
  const describedBy =
    [error ? errorId : null, hint ? hintId : null].filter(Boolean).join(" ") ||
    undefined;

  return (
    <div className="space-y-1.5">
      <Label htmlFor={name}>{label}</Label>
      <Textarea
        id={name}
        rows={rows}
        placeholder={placeholder}
        maxLength={maxLength}
        aria-invalid={error ? true : undefined}
        aria-describedby={describedBy}
        {...register(name)}
      />
      {hint ? (
        <p id={hintId} className="text-xs text-muted-foreground">
          {hint}
        </p>
      ) : null}
      {error ? (
        <p id={errorId} className="text-xs text-destructive">
          {error}
        </p>
      ) : null}
    </div>
  );
}

"use client";

import { useFormContext } from "react-hook-form";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { getFieldError } from "./field-error";

export function NumberField({
  name,
  label,
  hint,
  placeholder,
  min,
  max,
  required,
}: {
  name: string;
  label: string;
  hint?: string;
  placeholder?: string;
  min?: number;
  max?: number;
  required?: boolean;
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
      <Label htmlFor={name}>
        {label}
        {required ? <span aria-hidden="true"> *</span> : null}
      </Label>
      <Input
        id={name}
        type="number"
        min={min}
        max={max}
        placeholder={placeholder}
        aria-invalid={error ? true : undefined}
        aria-describedby={describedBy}
        {...register(name, { valueAsNumber: true })}
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

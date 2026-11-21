import type { FieldErrors } from "react-hook-form";

/**
 * Resolves a dotted/numeric field path (e.g. "questions.0.option1") through a
 * react-hook-form `FieldErrors` object (nested objects and arrays) and returns
 * the first `.message` string found at that path, or `undefined`.
 */
export function getFieldError(
  errors: FieldErrors,
  path: string,
): string | undefined {
  if (!errors) return undefined;

  let current: unknown = errors;

  for (const segment of path.split(".")) {
    if (current == null || typeof current !== "object") return undefined;

    if (Array.isArray(current)) {
      const index = Number(segment);
      if (!Number.isInteger(index)) return undefined;
      current = current[index];
    } else {
      current = (current as Record<string, unknown>)[segment];
    }
  }

  if (current != null && typeof current === "object" && "message" in current) {
    const message = (current as { message?: unknown }).message;
    return typeof message === "string" ? message : undefined;
  }

  return undefined;
}

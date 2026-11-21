import { toast } from "sonner";

/** Surfaces a not-yet-built feature without leaving a dead click. */
export function comingSoon(feature: string): void {
  toast.info(`${feature} — coming soon.`);
}

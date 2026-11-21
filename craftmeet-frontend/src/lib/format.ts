const relativeTime = new Intl.RelativeTimeFormat("en", { numeric: "auto" });

const UNITS: Array<[Intl.RelativeTimeFormatUnit, number]> = [
  ["year", 365 * 24 * 60 * 60 * 1000],
  ["month", 30 * 24 * 60 * 60 * 1000],
  ["week", 7 * 24 * 60 * 60 * 1000],
  ["day", 24 * 60 * 60 * 1000],
  ["hour", 60 * 60 * 1000],
  ["minute", 60 * 1000],
];

/** Formats an ISO timestamp as a short relative time ("3 days ago"). */
export function formatRelativeTime(
  iso: string,
  now: number = Date.now(),
): string {
  const diffMs = new Date(iso).getTime() - now;
  for (const [unit, ms] of UNITS) {
    if (Math.abs(diffMs) >= ms) {
      return relativeTime.format(Math.round(diffMs / ms), unit);
    }
  }
  return "just now";
}

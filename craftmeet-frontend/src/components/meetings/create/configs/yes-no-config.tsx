"use client";

export function YesNoConfig({ index }: { index: number }) {
  void index;
  return (
    <p className="text-sm text-muted-foreground">
      Participants vote Yes or No. Results shown as a live split. No extra config
      needed.
    </p>
  );
}

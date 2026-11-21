export type StepId = "basic" | "settings" | "questions" | "review";

export type Step = { id: StepId; title: string; subtitle: string };

export const STEPS: Step[] = [
  { id: "basic", title: "Basic Info", subtitle: "Name & describe your meeting" },
  {
    id: "settings",
    title: "Meeting Settings",
    subtitle: "Length & audience",
  },
  {
    id: "questions",
    title: "Questions",
    subtitle: "Compose your question set",
  },
  { id: "review", title: "Review & Submit", subtitle: "Confirm and create" },
];

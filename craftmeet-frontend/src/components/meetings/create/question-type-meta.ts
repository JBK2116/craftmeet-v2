import {
  AlignStartVertical,
  ChartBar,
  ListChecks,
  Star,
  ToggleLeft,
  type LucideIcon,
} from "lucide-react";
import type { QuestionType } from "@/lib/types/meeting";

export const QUESTION_TYPE_META: Record<
  QuestionType,
  { label: string; description: string; icon: LucideIcon }
> = {
  multiple_choice: {
    label: "Multiple Choice",
    description: "Pick from defined options",
    icon: ListChecks,
  },
  long_answer: {
    label: "Long Answer",
    description: "Open text response",
    icon: AlignStartVertical,
  },
  ranked_voting: {
    label: "Ranked Voting",
    description: "Prioritize a list of items",
    icon: ChartBar,
  },
  rating_scale: {
    label: "Rating Scale",
    description: "Numeric score within a range",
    icon: Star,
  },
  yes_no: {
    label: "Yes / No",
    description: "Simple binary vote",
    icon: ToggleLeft,
  },
};

import type {
  MeetingFormQuestion,
  MeetingFormValues,
} from "@/lib/schemas/meeting";
import { QUESTION_TYPE_META } from "./question-type-meta";

/** One-line summary of a question's type-specific configuration. */
function summarizeConfig(question: MeetingFormQuestion): string {
  switch (question.type) {
    case "multiple_choice": {
      const options = [
        question.option1,
        question.option2,
        question.option3,
        question.option4,
      ].filter((option) => option.trim().length > 0);
      return `Options: ${options.join(", ")}${
        question.allowMultiple ? " · multiple selections allowed" : ""
      }`;
    }
    case "long_answer":
      return `Open response · up to ${question.maxLength} characters`;
    case "ranked_voting": {
      const items = [
        question.item1,
        question.item2,
        question.item3,
        question.item4,
      ].filter((item) => item.trim().length > 0);
      return `Rank: ${items.join(", ")}`;
    }
    case "rating_scale":
      return `${question.min}–${question.max} rating scale`;
    case "yes_no":
      return "Yes / No vote";
  }
}

/** Read-only summary of the meeting draft shown before submission. */
export function StepReview({ values }: { values: MeetingFormValues }) {
  return (
    <div className="space-y-8">
      <section>
        <h3 className="text-sm font-semibold">Basic info</h3>
        <dl className="mt-3 space-y-3">
          <div>
            <dt className="text-xs font-medium text-muted-foreground">Title</dt>
            <dd className="mt-0.5 text-sm">{values.title || "—"}</dd>
          </div>
          <div>
            <dt className="text-xs font-medium text-muted-foreground">
              Description
            </dt>
            <dd className="mt-0.5 text-sm whitespace-pre-wrap">
              {values.description || "—"}
            </dd>
          </div>
        </dl>
      </section>

      <section>
        <h3 className="text-sm font-semibold">Settings</h3>
        <dl className="mt-3 space-y-3">
          <div>
            <dt className="text-xs font-medium text-muted-foreground">
              Duration
            </dt>
            <dd className="mt-0.5 text-sm">{values.duration} minutes</dd>
          </div>
          <div>
            <dt className="text-xs font-medium text-muted-foreground">
              Participant cap
            </dt>
            <dd className="mt-0.5 text-sm">{values.participantCap} people</dd>
          </div>
        </dl>
      </section>

      <section>
        <h3 className="text-sm font-semibold">Questions</h3>
        <ol className="mt-3 space-y-3">
          {values.questions.map((question, index) => (
            <li key={index} className="rounded-lg border border-border p-3">
              <div className="flex items-center gap-2 text-xs text-muted-foreground">
                <span className="font-medium">Q{index + 1}</span>
                <span aria-hidden="true">·</span>
                <span>{QUESTION_TYPE_META[question.type].label}</span>
              </div>
              <p className="mt-1.5 text-sm">{question.prompt}</p>
              <p className="mt-1 text-xs text-muted-foreground">
                {summarizeConfig(question)}
              </p>
            </li>
          ))}
        </ol>
      </section>
    </div>
  );
}

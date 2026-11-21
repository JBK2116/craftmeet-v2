import { z } from "zod";

import {
  DEFAULT_DURATION,
  DEFAULT_MAX_LENGTH,
  DEFAULT_PARTICIPANT_CAP,
  DEFAULT_RATING,
  MEETING_LIMITS,
} from "@/lib/meeting-constants";
import type {
  MeetingQuestionRequest,
  MeetingRequest,
  QuestionType,
} from "@/lib/types/meeting";

const LIMITS = MEETING_LIMITS;

const promptSchema = z
  .string()
  .trim()
  .min(LIMITS.prompt.min, { error: "Prompt is required." })
  .max(LIMITS.prompt.max, {
    error: `Prompt must be ${LIMITS.prompt.max} characters or fewer.`,
  });

const optionSchema = z
  .string()
  .trim()
  .min(LIMITS.option.min, { error: "Option is required." })
  .max(LIMITS.option.max, {
    error: `Option must be ${LIMITS.option.max} characters or fewer.`,
  });

const itemSchema = z
  .string()
  .trim()
  .min(LIMITS.option.min, { error: "Item is required." })
  .max(LIMITS.option.max, {
    error: `Item must be ${LIMITS.option.max} characters or fewer.`,
  });

const optionalOptionSchema = z.string();

const multipleChoiceSchema = z.object({
  type: z.literal("multiple_choice"),
  prompt: promptSchema,
  option1: optionSchema,
  option2: optionSchema,
  option3: optionalOptionSchema,
  option4: optionalOptionSchema,
  allowMultiple: z.boolean(),
});

const longAnswerSchema = z.object({
  type: z.literal("long_answer"),
  prompt: promptSchema,
  maxLength: z.coerce
    .number()
    .min(LIMITS.maxLength.min, { error: "Maximum length must be at least 1." })
    .max(LIMITS.maxLength.max, {
      error: `Maximum length must be ${LIMITS.maxLength.max} or fewer.`,
    }),
});

const rankedVotingSchema = z.object({
  type: z.literal("ranked_voting"),
  prompt: promptSchema,
  item1: itemSchema,
  item2: itemSchema,
  item3: optionalOptionSchema,
  item4: optionalOptionSchema,
});

const ratingScaleSchema = z
  .object({
    type: z.literal("rating_scale"),
    prompt: promptSchema,
    min: z.coerce.number().min(LIMITS.rating.min).max(LIMITS.rating.max),
    max: z.coerce.number().min(LIMITS.rating.min).max(LIMITS.rating.max),
  })
  .refine((rating) => rating.min < rating.max, {
    error: "Maximum rating must be greater than minimum rating.",
    path: ["max"],
  });

const yesNoSchema = z.object({
  type: z.literal("yes_no"),
  prompt: promptSchema,
});

const questionSchema = z.discriminatedUnion("type", [
  multipleChoiceSchema,
  longAnswerSchema,
  rankedVotingSchema,
  ratingScaleSchema,
  yesNoSchema,
]);

export const meetingFormSchema = z.object({
  title: z
    .string()
    .trim()
    .min(LIMITS.title.min, { error: "Title is required." })
    .max(LIMITS.title.max, {
      error: `Title must be ${LIMITS.title.max} characters or fewer.`,
    }),
  description: z.string().max(LIMITS.description.max, {
    error: `Description must be ${LIMITS.description.max} characters or fewer.`,
  }),
  duration: z.coerce
    .number()
    .int({ error: "Duration must be a whole number." })
    .min(LIMITS.duration.min, { error: "Duration must be at least 1 minute." })
    .max(LIMITS.duration.max, {
      error: `Duration must be ${LIMITS.duration.max} minutes or fewer.`,
    }),
  participantCap: z.coerce
    .number()
    .int({ error: "Participant cap must be a whole number." })
    .min(LIMITS.participantCap.min, {
      error: "Participant cap must be at least 1.",
    })
    .max(LIMITS.participantCap.max, {
      error: `Participant cap must be ${LIMITS.participantCap.max} or fewer.`,
    }),
  questions: z
    .array(questionSchema)
    .min(LIMITS.questions.min, { error: "Add at least one question." })
    .max(LIMITS.questions.max, {
      error: `You can add up to ${LIMITS.questions.max} questions.`,
    }),
});

export type MeetingFormValues = z.infer<typeof meetingFormSchema>;

export type MeetingFormQuestion = MeetingFormValues["questions"][number];

export const DEFAULT_MEETING_VALUES: MeetingFormValues = {
  title: "",
  description: "",
  duration: DEFAULT_DURATION,
  participantCap: DEFAULT_PARTICIPANT_CAP,
  questions: [],
};

export function newQuestion(type: QuestionType): MeetingFormQuestion {
  switch (type) {
    case "multiple_choice":
      return {
        type: "multiple_choice",
        prompt: "",
        option1: "",
        option2: "",
        option3: "",
        option4: "",
        allowMultiple: false,
      };
    case "long_answer":
      return {
        type: "long_answer",
        prompt: "",
        maxLength: DEFAULT_MAX_LENGTH,
      };
    case "ranked_voting":
      return {
        type: "ranked_voting",
        prompt: "",
        item1: "",
        item2: "",
        item3: "",
        item4: "",
      };
    case "rating_scale":
      return {
        type: "rating_scale",
        prompt: "",
        min: DEFAULT_RATING.min,
        max: DEFAULT_RATING.max,
      };
    case "yes_no":
      return { type: "yes_no", prompt: "" };
  }
}

function toRequestQuestion(
  question: MeetingFormQuestion,
  position: number,
): MeetingQuestionRequest {
  switch (question.type) {
    case "multiple_choice":
      return {
        type: "multiple_choice",
        prompt: question.prompt.trim(),
        position,
        option1: question.option1,
        option2: question.option2,
        option3: question.option3 || null,
        option4: question.option4 || null,
        allowMultiple: question.allowMultiple,
      };
    case "long_answer":
      return {
        type: "long_answer",
        prompt: question.prompt.trim(),
        position,
        maxLength: question.maxLength,
      };
    case "ranked_voting":
      return {
        type: "ranked_voting",
        prompt: question.prompt.trim(),
        position,
        item1: question.item1,
        item2: question.item2,
        item3: question.item3 || null,
        item4: question.item4 || null,
      };
    case "rating_scale":
      return {
        type: "rating_scale",
        prompt: question.prompt.trim(),
        position,
        min: question.min,
        max: question.max,
      };
    case "yes_no":
      return {
        type: "yes_no",
        prompt: question.prompt.trim(),
        position,
      };
  }
}

export function toMeetingRequest(values: MeetingFormValues): MeetingRequest {
  return {
    title: values.title.trim(),
    description: values.description.trim() || null,
    duration: values.duration,
    participantCap: values.participantCap,
    questions: values.questions.map((question, index) =>
      toRequestQuestion(question, index + 1),
    ),
  };
}

/** Mirrors the backend `MeetingResponse` / `StatResponse` / question `type`
 *  discriminator. IDs and timestamps arrive as JSON strings. */
export type MeetingStatus = "live" | "draft" | "ended";

export type QuestionType =
  | "multiple_choice"
  | "long_answer"
  | "ranked_voting"
  | "rating_scale"
  | "yes_no";

/** A persisted question as returned by the backend (`QuestionResponse`).
 *  Jackson-polymorphic on `type`, so each variant carries its type-specific
 *  fields; nullable columns arrive as `null`. */
type QuestionBase = {
  id: string;
  meetingId: string;
  prompt: string;
  position: number;
};

export type MultipleChoiceQuestion = QuestionBase & {
  type: "multiple_choice";
  option1: string;
  option2: string;
  option3: string | null;
  option4: string | null;
  allowMultiple: boolean | null;
};

export type LongAnswerQuestion = QuestionBase & {
  type: "long_answer";
  maxLength: number | null;
};

export type RankedVotingQuestion = QuestionBase & {
  type: "ranked_voting";
  item1: string;
  item2: string;
  item3: string | null;
  item4: string | null;
};

export type RatingScaleQuestion = QuestionBase & {
  type: "rating_scale";
  min: number | null;
  max: number | null;
};

export type YesNoQuestion = QuestionBase & {
  type: "yes_no";
};

export type Question =
  | MultipleChoiceQuestion
  | LongAnswerQuestion
  | RankedVotingQuestion
  | RatingScaleQuestion
  | YesNoQuestion;

export type MeetingStat = {
  id: string;
  meetingId: string;
  totalParticipants: number;
  totalQuestionsAsked: number;
  totalResponsesReceived: number;
  durationSeconds: number | null;
};

export type Meeting = {
  id: string;
  userId: string;
  title: string;
  description: string | null;
  roomCode: string;
  duration: number;
  participantCap: number;
  startTime: string | null;
  endTime: string | null;
  createdAt: string;
  updatedAt: string;
  questions: Question[];
  statResponse: MeetingStat;
};

export type MultipleChoiceQuestionRequest = {
  type: "multiple_choice";
  prompt: string;
  position: number;
  option1: string;
  option2: string;
  option3: string | null;
  option4: string | null;
  allowMultiple: boolean;
};

export type LongAnswerQuestionRequest = {
  type: "long_answer";
  prompt: string;
  position: number;
  maxLength: number;
};

export type RankedVotingQuestionRequest = {
  type: "ranked_voting";
  prompt: string;
  position: number;
  item1: string;
  item2: string;
  item3: string | null;
  item4: string | null;
};

export type RatingScaleQuestionRequest = {
  type: "rating_scale";
  prompt: string;
  position: number;
  min: number;
  max: number;
};

export type YesNoQuestionRequest = {
  type: "yes_no";
  prompt: string;
  position: number;
};

export type MeetingQuestionRequest =
  | MultipleChoiceQuestionRequest
  | LongAnswerQuestionRequest
  | RankedVotingQuestionRequest
  | RatingScaleQuestionRequest
  | YesNoQuestionRequest;

export type MeetingRequest = {
  title: string;
  description: string | null;
  duration: number;
  participantCap: number;
  questions: MeetingQuestionRequest[];
};

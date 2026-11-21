/** Mirrors the backend `MeetingResponse` / `StatResponse` / question `type`
 *  discriminator. IDs and timestamps arrive as JSON strings. */
export type MeetingStatus = "live" | "draft" | "ended";

export type QuestionType =
  | "multiple_choice"
  | "long_answer"
  | "ranked_voting"
  | "rating_scale"
  | "yes_no";

/** The subset of a question the dashboard needs. Type-specific fields are
 *  added when the create/edit/host pages are built. */
export type Question = {
  id: string;
  meetingId: string;
  prompt: string;
  position: number;
  type: QuestionType;
};

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

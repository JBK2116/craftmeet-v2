export const MEETING_LIMITS = {
  title: { min: 1, max: 150 },
  description: { max: 2000 },
  duration: { min: 1, max: 240 },
  participantCap: { min: 1, max: 250 },
  questions: { min: 1, max: 50 },
  prompt: { min: 1, max: 500 },
  option: { min: 1, max: 200 },
  maxLength: { min: 1, max: 5000 },
  rating: { min: 0, max: 10 },
} as const;

export const DEFAULT_DURATION = 30;
export const DEFAULT_PARTICIPANT_CAP = 50;
export const DEFAULT_MAX_LENGTH = 500;
export const DEFAULT_RATING = { min: 0, max: 5 };

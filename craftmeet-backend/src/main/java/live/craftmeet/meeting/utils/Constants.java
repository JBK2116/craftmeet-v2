package live.craftmeet.meeting.utils;

/**
 * Centralizes the size, length, and count limits for meeting and question
 * fields. These constants define the allowed bounds used for validation,
 * entity column definitions, and database check constraints.
 */
public final class Constants {
    private Constants() {
    }

    /** Maximum duration of a meeting, in minutes. */
    public static final int MAX_MEETING_DURATION = 60 * 4;
    /** Minimum duration of a meeting, in minutes. */
    public static final int MIN_MEETING_DURATION = 1;

    /** Maximum length of a meeting title, in characters. */
    public static final int MAX_TITLE_LENGTH = 150;
    /** Minimum length of a meeting title, in characters. */
    public static final int MIN_TITLE_LENGTH = 1;

    /** Maximum length of a meeting description, in characters. */
    public static final int MAX_DESCRIPTION_LENGTH = 2000;
    /** Minimum length of a meeting description, in characters. */
    public static final int MIN_DESCRIPTION_LENGTH = 1;

    /** Maximum number of questions allowed in a single meeting. */
    public static final int MAX_QUESTION_COUNT = 50;
    /** Minimum number of questions required in a single meeting. */
    public static final int MIN_QUESTION_COUNT = 1;

    /** Maximum number of participants allowed to join a single meeting. */
    public static final int MAX_PARTICIPANT_COUNT = 250;
    /** Minimum number of participants allowed to join a single meeting. */
    public static final int MIN_PARTICIPANT_COUNT = 1;

    /** Length of the alphanumeric code used to join a meeting. */
    public static final int MEETING_CODE_LENGTH = 8;

    /** Maximum length of a question's prompt text, in characters. */
    public static final int MAX_QUESTION_TEXT_LENGTH = 500;
    /** Minimum length of a question's prompt text, in characters. */
    public static final int MIN_QUESTION_TEXT_LENGTH = 1;

    /** Maximum length of a single multiple-choice option, in characters. */
    public static final int MAX_MULTIPLE_OPTION_TEXT_LENGTH = 200;
    /** Minimum length of a single multiple-choice option, in characters. */
    public static final int MIN_MULTIPLE_OPTION_TEXT_LENGTH = 1;

    /** Maximum length of a long-answer response, in characters. */
    public static final int MAX_LONG_ANSWER_TEXT_LENGTH = 5000;
    /** Minimum length of a long-answer response, in characters. */
    public static final int MIN_LONG_ANSWER_TEXT_LENGTH = 1;

    /** Upper bound of a rating-scale question. */
    public static final int MAX_RATING_SCALE_VALUE = 10;
    /** Lower bound of a rating-scale question. */
    public static final int MIN_RATING_SCALE_VALUE = 0;
}

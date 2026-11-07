package live.craftmeet.user.utils;

/**
 * Centralizes length limits for user-related fields.
 * These constants define the allowed minimum
 * and maximum lengths used for validation.
 */
public final class Constants {
    private Constants() {
    }

    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 30;

    public static final int MIN_EMAIL_LENGTH = 3;
    public static final int MAX_EMAIL_LENGTH = 254;

    public static final int MIN_PASSWORD_LENGTH = 12;
    public static final int MAX_PASSWORD_LENGTH = 72;

    public static final int MAX_GOOGLE_ID_LENGTH = 255;

}
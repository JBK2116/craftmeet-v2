package live.craftmeet.authentication.oauth;

/**
 * Immutable record containing the Google account identifiers needed to
 * provision or link a local user: the Google subject ({@code sub}) and the
 * account's email address.
 */
public record GoogleUserInfo(String sub, String email) {
}

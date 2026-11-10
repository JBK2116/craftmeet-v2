package live.craftmeet.authentication.token;

/**
 * Identifies the kind of token issued by the authentication service.
 */
public enum TokenType {
    /**
     * Represents an access token and the type value stored in its JWT claim.
     */
    ACCESS("refresh"),
    VERIFY_EMAIL("verify_email"),
    RESET_PASSWORD("reset_password");

    /**
     * String value used when the token type is written to a JWT claim.
     */
    private final String type;

    /**
     * Creates a token type with its serialized claim value.
     *
     * @param type the value stored in the token's {@code type} claim
     */
    TokenType(String type) {
        this.type = type;
    }

    /**
     * Returns the serialized value for this token type.
     *
     * @return the token type value used in the JWT claim
     */
    public String getType() {
        return type;
    }
}

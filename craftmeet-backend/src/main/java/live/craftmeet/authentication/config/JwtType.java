package live.craftmeet.authentication.config;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the categories of JSON Web Tokens supported by the authentication
 * system.
 * The categories distinguish tokens intended for access from tokens intended
 * for refresh.
 */
@Getter
public enum JwtType {
    ACCESS("access_token"), REFRESH("refresh_token");

    private final String type;

    private static final Map<String, JwtType> BY_TYPE_VALUE = new HashMap<>();

    static {
        for (JwtType jwtType : values()) {
            BY_TYPE_VALUE.put(jwtType.type.toLowerCase(), jwtType);
        }
    }

    JwtType(String type) {
        this.type = type;
    }

    /**
     * Resolves the JwtType matching the supplied token type string.
     *
     * @param jwtType the token type string to resolve; must not be null and must
     *                correspond to a supported token type
     * @return the JwtType matching the supplied token type string
     * @throws IllegalArgumentException if jwtType is null or does not match a
     *                                  supported token type
     */
    public static JwtType fromString(String jwtType) {
        if (jwtType == null) {
            throw new IllegalArgumentException("type cannot be null");
        }

        JwtType match = BY_TYPE_VALUE.get(jwtType.toLowerCase());
        if (match == null) {
            throw new IllegalArgumentException("type is invalid: " + jwtType);
        }

        return match;
    }
}

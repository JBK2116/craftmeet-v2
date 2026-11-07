package live.craftmeet.user.dto;

import live.craftmeet.user.model.User;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable response model containing user account information returned to clients.
 * Represents the outgoing user data exposed by user-related operations.
 */
public record UserResponse(
        UUID id,
        String username,
        String email,
        boolean verified,
        Instant verified_at
) {
    /**
     * Creates a response model from the given user entity.
     *
     * @param user the user entity to convert
     * @return a new response model populated with the user's account information
     */
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.isVerified(), user.getVerifiedAt());
    }
}

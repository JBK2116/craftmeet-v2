package live.craftmeet.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import live.craftmeet.user.utils.Constants;


/**
 * Request payload for updating an existing user's username.
 */
public record UpdateUsernameRequest(
        @NotBlank
        @Size(min = Constants.MIN_USERNAME_LENGTH, max = Constants.MAX_USERNAME_LENGTH, message = "Username must be between " + Constants.MIN_USERNAME_LENGTH + " and " + Constants.MAX_USERNAME_LENGTH + " characters")
        @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Username must only contain alphanumeric characters")
        String newUsername
) {

}

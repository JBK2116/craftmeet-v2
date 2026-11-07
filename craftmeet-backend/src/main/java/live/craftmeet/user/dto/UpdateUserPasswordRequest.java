package live.craftmeet.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import live.craftmeet.user.utils.Constants;

public record UpdateUserPasswordRequest(
        @NotBlank
        @Size(min = Constants.MIN_PASSWORD_LENGTH, max = Constants.MAX_PASSWORD_LENGTH, message = "Old password must be between 12 and 72 characters")
        String oldPassword,
        @NotBlank
        @Size(min = Constants.MIN_PASSWORD_LENGTH, max = Constants.MAX_PASSWORD_LENGTH, message = "New password must be between 12 and 72 characters")
        String newPassword
) {

    public UpdateUserPasswordRequest {
        if (oldPassword.equals(newPassword)) {
            throw new IllegalArgumentException("Old password cannot be the same as new password");
        }
    }
}

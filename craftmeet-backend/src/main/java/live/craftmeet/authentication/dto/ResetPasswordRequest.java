package live.craftmeet.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import live.craftmeet.user.utils.Constants;

public record ResetPasswordRequest(
        @NotBlank(message = "New password is required") @Size(min = Constants.MIN_PASSWORD_LENGTH, max = Constants.MAX_PASSWORD_LENGTH, message = "New password must be between {min} and {max} characters") String newPassword) {
    public ResetPasswordRequest {
    }
}

package live.craftmeet.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import live.craftmeet.user.utils.Constants;

public record LoginRequest(
        @NotBlank(message = "Email is required") @Size(min = Constants.MIN_EMAIL_LENGTH, max = Constants.MAX_EMAIL_LENGTH, message = "Email must be between "
                + Constants.MIN_EMAIL_LENGTH + " and " + Constants.MAX_EMAIL_LENGTH + " characters") String email,
        @NotBlank(message = "Password is required") @Size(min = Constants.MIN_PASSWORD_LENGTH, max = Constants.MAX_PASSWORD_LENGTH, message = "Password must be between "
                + Constants.MIN_PASSWORD_LENGTH + " and " + Constants.MAX_PASSWORD_LENGTH
                + " characters") String password) {
}

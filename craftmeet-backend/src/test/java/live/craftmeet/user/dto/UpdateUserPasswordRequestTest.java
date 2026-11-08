package live.craftmeet.user.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Validates the constraint rules declared on {@link UpdateUserPasswordRequest},
 * including the constructor-level guard against reusing the current password.
 */
class UpdateUserPasswordRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validPasswords_passValidation() {
        var request = new UpdateUserPasswordRequest("oldPassword123", "newPassword456");
        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void sameOldAndNewPassword_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new UpdateUserPasswordRequest("password12345", "password12345"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tooShortPassword_failsValidation() {
        var request = new UpdateUserPasswordRequest("short1", "short2");
        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void blankPassword_failsValidation() {
        var request = new UpdateUserPasswordRequest("", "newPassword456");
        assertThat(validator.validate(request)).isNotEmpty();
    }
}

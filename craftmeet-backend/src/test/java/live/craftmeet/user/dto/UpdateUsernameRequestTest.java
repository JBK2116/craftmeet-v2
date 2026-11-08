package live.craftmeet.user.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Validates the constraint rules declared on {@link UpdateUsernameRequest}.
 */
class UpdateUsernameRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validUsername_passesValidation() {
        var request = new UpdateUsernameRequest("johndoe");
        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void blankUsername_failsValidation() {
        var request = new UpdateUsernameRequest("");
        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void nullUsername_failsValidation() {
        var request = new UpdateUsernameRequest(null);
        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void tooShortUsername_failsValidation() {
        var request = new UpdateUsernameRequest("ab");
        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void tooLongUsername_failsValidation() {
        var request = new UpdateUsernameRequest("a".repeat(31));
        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void nonAlphanumericUsername_failsValidation() {
        var request = new UpdateUsernameRequest("john-doe");
        assertThat(validator.validate(request)).isNotEmpty();
    }
}

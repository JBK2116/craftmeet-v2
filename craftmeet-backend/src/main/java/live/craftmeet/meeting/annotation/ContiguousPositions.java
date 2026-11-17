package live.craftmeet.meeting.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ContiguousPositionsValidator.class)
public @interface ContiguousPositions {
	String message() default "Question positions must be unique and contiguous";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}

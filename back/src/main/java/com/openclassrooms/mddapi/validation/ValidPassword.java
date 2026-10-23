package com.openclassrooms.mddapi.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Password rule of the functional specifications: at least 8 characters with at least one digit,
 * one lowercase letter, one uppercase letter and one special character.
 * The 64-character maximum follows the OWASP recommendation and bounds the cost of hashing.
 * A null value is accepted, so that the rule also fits an optional password; add {@code @NotNull} when required.
 */
@Documented
@Constraint(validatedBy = {})
@Target({ FIELD, PARAMETER })
@Retention(RUNTIME)
@Size(min = 8, max = 64)
@Pattern(regexp = ValidPassword.PATTERN)
@ReportAsSingleViolation
public @interface ValidPassword {

	/** A digit, a lowercase letter, an uppercase letter and a character that is neither a letter nor a digit. */
	String PATTERN = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).*$";

	String message() default "Password must be 8 to 64 characters long and contain a digit, a lowercase letter, "
			+ "an uppercase letter and a special character.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Username rule: 3 to 30 letters, digits, dots, hyphens or underscores.
 * It never contains {@code @}, so that a login identifier is unambiguously either an email or a username.
 */
@Documented
@Constraint(validatedBy = {})
@Target({ FIELD, PARAMETER })
@Retention(RUNTIME)
@NotBlank
@Size(min = 3, max = 30)
@Pattern(regexp = "^[A-Za-z0-9._-]+$")
@ReportAsSingleViolation
public @interface ValidUsername {

	String message() default "Username must be 3 to 30 characters long and may only contain letters, digits, dots, "
			+ "hyphens and underscores.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}

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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Email rule of an account: a valid address of at most {@value ValidationLimits#EMAIL_MAX} characters.
 */
@Documented
@Constraint(validatedBy = {})
@Target({ FIELD, PARAMETER })
@Retention(RUNTIME)
@NotBlank
@Email
@Size(max = ValidationLimits.EMAIL_MAX)
@ReportAsSingleViolation
public @interface ValidEmail {

	String message() default "Email must be a valid address of at most " + ValidationLimits.EMAIL_MAX + " characters.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}

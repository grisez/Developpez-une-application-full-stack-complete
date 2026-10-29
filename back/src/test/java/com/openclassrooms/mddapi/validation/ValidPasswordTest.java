package com.openclassrooms.mddapi.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.openclassrooms.mddapi.dto.RegisterRequest;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * The password rule of the specifications, checked on the registration request: the back-end is the reference,
 * the same rule in the front-end only gives earlier feedback.
 */
class ValidPasswordTest {

	private static ValidatorFactory factory;

	private static Validator validator;

	@BeforeAll
	static void createValidator() {
		factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@AfterAll
	static void closeValidator() {
		factory.close();
	}

	private static boolean isAccepted(String password) {
		return validator.validateValue(RegisterRequest.class, "password", password).isEmpty();
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"Aa1!aaaa", // 8 characters, the minimum
			"Str0ng!Password",
			"Aa1!aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", // 64 characters, the maximum
			"Aa1 aaaa", // a space counts as a special character
	})
	void acceptsAPasswordThatFollowsEveryRule(String password) {
		// Act and assert
		assertThat(isAccepted(password)).isTrue();
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"Aa1!aaa", // 7 characters
			"Aa1!aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", // 65 characters
			"Aa!aaaaa", // no digit
			"AA1!AAAA", // no lowercase letter
			"aa1!aaaa", // no uppercase letter
			"Aa1aaaaa", // no special character
	})
	void refusesAPasswordThatBreaksARule(String password) {
		// Act and assert
		assertThat(isAccepted(password)).isFalse();
	}

}

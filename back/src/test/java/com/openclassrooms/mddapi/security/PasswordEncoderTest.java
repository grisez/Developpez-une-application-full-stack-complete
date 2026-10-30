package com.openclassrooms.mddapi.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class PasswordEncoderTest {

	private final PasswordEncoder passwordEncoder = new SecurityConfig().passwordEncoder();

	@Test
	void hashIsArgon2idAndFitsTheDatabaseColumn() {
		// Act
		String hash = passwordEncoder.encode("Str0ng!Password");

		// Assert
		assertThat(hash).startsWith("$argon2id$").hasSizeLessThanOrEqualTo(128);
		assertThat(passwordEncoder.matches("Str0ng!Password", hash)).isTrue();
		assertThat(passwordEncoder.matches("wrong-password", hash)).isFalse();
	}

}

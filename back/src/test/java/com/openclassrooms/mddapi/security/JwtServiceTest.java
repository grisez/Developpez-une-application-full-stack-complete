package com.openclassrooms.mddapi.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import com.openclassrooms.mddapi.model.User;

class JwtServiceTest {

	private static final JwtProperties PROPERTIES =
			new JwtProperties("test-only-secret-key-of-at-least-32-characters", Duration.ofDays(7), false);

	private JwtService jwtService;

	private JwtDecoder jwtDecoder;

	@BeforeEach
	void setUp() {
		SecurityConfig securityConfig = new SecurityConfig();
		jwtService = new JwtService(securityConfig.jwtEncoder(PROPERTIES), PROPERTIES);
		jwtDecoder = securityConfig.jwtDecoder(PROPERTIES);
	}

	@Test
	void generatedTokenIdentifiesTheUserAndExpiresAfterTheConfiguredDuration() {
		// Arrange
		User user = new User();
		user.setId(42L);

		// Act
		Jwt jwt = jwtDecoder.decode(jwtService.generateToken(user));

		// Assert
		assertThat(jwt.getSubject()).isEqualTo("42");
		assertThat(jwt.getClaimAsString("iss")).isEqualTo(JwtService.ISSUER);
		assertThat(Duration.between(jwt.getIssuedAt(), jwt.getExpiresAt())).isEqualTo(Duration.ofDays(7));
	}

}

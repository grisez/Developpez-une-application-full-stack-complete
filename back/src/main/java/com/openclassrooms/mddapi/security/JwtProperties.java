package com.openclassrooms.mddapi.security;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Settings of the JWT that authenticates users, bound from the {@code mdd.security.jwt} properties.
 * The application refuses to start when the secret is missing or too short.
 *
 * @param secret       HMAC signing key; HS256 requires at least 256 bits, hence 32 characters
 * @param expiration   lifetime of a token, and of the cookie that carries it
 * @param cookieSecure whether the cookie is only sent over HTTPS; disabled for local HTTP development
 */
@Validated
@ConfigurationProperties(prefix = "mdd.security.jwt")
public record JwtProperties(
		@NotBlank @Size(min = 32) String secret,
		@NotNull Duration expiration,
		boolean cookieSecure) {
}

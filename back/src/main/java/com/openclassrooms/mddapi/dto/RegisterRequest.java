package com.openclassrooms.mddapi.dto;

import org.springframework.util.StringUtils;

import com.openclassrooms.mddapi.validation.ValidPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data sent by a visitor to create an account.
 *
 * @param email    email address, unique among users
 * @param username username, unique among users; it never contains {@code @}, so that a login identifier
 *                 is unambiguously either an email or a username
 * @param password plain password, hashed before being stored
 */
public record RegisterRequest(
		@NotBlank @Email @Size(max = 254) String email,
		@NotBlank @Size(min = 3, max = 30)
		@Pattern(regexp = "^[A-Za-z0-9._-]+$",
				message = "Username may only contain letters, digits, dots, hyphens and underscores.")
		String username,
		@ValidPassword String password) {

	/**
	 * Removes the surrounding spaces before validation runs, so that a trailing space typed by mistake
	 * is not reported as an invalid email or username. The password is kept as typed: a space is a valid
	 * password character.
	 */
	public RegisterRequest {
		email = StringUtils.trimWhitespace(email);
		username = StringUtils.trimWhitespace(username);
	}

}

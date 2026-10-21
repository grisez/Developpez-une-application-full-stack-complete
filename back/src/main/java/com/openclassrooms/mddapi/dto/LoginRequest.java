package com.openclassrooms.mddapi.dto;

import org.springframework.util.StringUtils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Credentials sent to log in. The password rule is not checked here: any wrong password is simply rejected.
 *
 * @param identifier the email or the username of the account
 * @param password   the plain password
 */
public record LoginRequest(
		@NotBlank @Size(max = 254) String identifier,
		@NotBlank @Size(max = 64) String password) {

	/**
	 * Removes the surrounding spaces of the identifier before validation runs. The password is kept as typed:
	 * a space is a valid password character.
	 */
	public LoginRequest {
		identifier = StringUtils.trimWhitespace(identifier);
	}

}

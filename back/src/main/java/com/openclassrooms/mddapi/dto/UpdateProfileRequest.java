package com.openclassrooms.mddapi.dto;

import org.springframework.util.StringUtils;

import com.openclassrooms.mddapi.validation.ValidEmail;
import com.openclassrooms.mddapi.validation.ValidPassword;
import com.openclassrooms.mddapi.validation.ValidUsername;

/**
 * Changes made by a user to their profile, with the same rules as the registration.
 *
 * @param email    the new email address, unique among users
 * @param username the new username, unique among users
 * @param password the new password, or null to keep the current one
 */
public record UpdateProfileRequest(
		@ValidEmail String email,
		@ValidUsername String username,
		@ValidPassword String password) {

	/**
	 * Removes the surrounding spaces of the email and the username before validation runs. An empty password
	 * means that the user keeps the current one; otherwise the password is kept as typed.
	 */
	public UpdateProfileRequest {
		email = StringUtils.trimWhitespace(email);
		username = StringUtils.trimWhitespace(username);
		password = StringUtils.hasLength(password) ? password : null;
	}

}

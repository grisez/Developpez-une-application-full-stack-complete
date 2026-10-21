package com.openclassrooms.mddapi.dto;

import com.openclassrooms.mddapi.validation.ValidPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data sent by a visitor to create an account.
 *
 * @param email    email address, unique among users
 * @param username username, unique among users
 * @param password plain password, hashed before being stored
 */
public record RegisterRequest(
		@NotBlank @Email @Size(max = 254) String email,
		@NotBlank @Size(min = 3, max = 30) String username,
		@ValidPassword String password) {
}

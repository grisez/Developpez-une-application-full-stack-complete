package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.LoginRequest;
import com.openclassrooms.mddapi.dto.RegisterRequest;

/**
 * Account creation and authentication.
 */
public interface AuthService {

	/**
	 * Creates an account and logs the new user in.
	 *
	 * @param request the registration data
	 * @return the created user and their token
	 * @throws com.openclassrooms.mddapi.exception.EmailAlreadyUsedException    if the email is already used
	 * @throws com.openclassrooms.mddapi.exception.UsernameAlreadyUsedException if the username is already used
	 */
	AuthResult register(RegisterRequest request);

	/**
	 * Logs a user in with their email or their username.
	 *
	 * @param request the credentials
	 * @return the user and their token
	 * @throws com.openclassrooms.mddapi.exception.InvalidCredentialsException if the account does not exist
	 *                                                                         or the password is wrong
	 */
	AuthResult login(LoginRequest request);

}

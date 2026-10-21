package com.openclassrooms.mddapi.exception;

/**
 * Thrown when a login fails, whether the account does not exist or the password is wrong:
 * the two cases are deliberately not distinguished, so that nobody can find out which accounts exist.
 */
public class InvalidCredentialsException extends RuntimeException {

	public InvalidCredentialsException() {
		super("Invalid identifier or password.");
	}

}

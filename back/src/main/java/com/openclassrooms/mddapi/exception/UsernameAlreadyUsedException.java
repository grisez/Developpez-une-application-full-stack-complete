package com.openclassrooms.mddapi.exception;

/**
 * Thrown when a username is already taken by another account.
 */
public class UsernameAlreadyUsedException extends RuntimeException {

	public UsernameAlreadyUsedException() {
		super("This username is already used.");
	}

}

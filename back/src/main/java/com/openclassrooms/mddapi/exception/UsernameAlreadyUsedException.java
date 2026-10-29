package com.openclassrooms.mddapi.exception;

/**
 * Thrown when a username is already taken by another account.
 */
public class UsernameAlreadyUsedException extends FieldAlreadyUsedException {

	public UsernameAlreadyUsedException() {
		super("username", "This username is already used.");
	}

}

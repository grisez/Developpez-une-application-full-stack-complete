package com.openclassrooms.mddapi.exception;

/**
 * Thrown when an email address is already linked to another account.
 */
public class EmailAlreadyUsedException extends RuntimeException {

	public EmailAlreadyUsedException() {
		super("This email address is already used.");
	}

}

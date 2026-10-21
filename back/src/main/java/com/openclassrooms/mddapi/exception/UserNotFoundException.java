package com.openclassrooms.mddapi.exception;

/**
 * Thrown when no user matches the requested id.
 */
public class UserNotFoundException extends RuntimeException {

	/**
	 * @param id the id that matched no user
	 */
	public UserNotFoundException(Long id) {
		super("No user found with id " + id);
	}

}

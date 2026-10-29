package com.openclassrooms.mddapi.exception;

/**
 * Thrown when no article matches the requested id.
 */
public class PostNotFoundException extends ResourceNotFoundException {

	/**
	 * @param id the id that matched no article
	 */
	public PostNotFoundException(Long id) {
		super("Article", id);
	}

}

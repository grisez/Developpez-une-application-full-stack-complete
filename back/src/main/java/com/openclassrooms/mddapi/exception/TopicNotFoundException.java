package com.openclassrooms.mddapi.exception;

/**
 * Thrown when no topic matches the requested id.
 */
public class TopicNotFoundException extends ResourceNotFoundException {

	/**
	 * @param id the id that matched no topic
	 */
	public TopicNotFoundException(Integer id) {
		super("Topic", id);
	}

}

package com.openclassrooms.mddapi.exception;

/**
 * Thrown when no resource matches the requested id. Each kind of resource has its own subclass, all answered with a
 * 404 by a single handler.
 */
public abstract class ResourceNotFoundException extends RuntimeException {

	private final String resource;

	/**
	 * @param resource the kind of resource, as shown to the client, for example "Article"
	 * @param id       the id that matched nothing, kept in the message for the server logs only
	 */
	protected ResourceNotFoundException(String resource, Object id) {
		super("No " + resource.toLowerCase() + " found with id " + id);
		this.resource = resource;
	}

	/**
	 * @return the message sent to the client, which does not repeat the id
	 */
	public String getPublicMessage() {
		return resource + " not found.";
	}

}

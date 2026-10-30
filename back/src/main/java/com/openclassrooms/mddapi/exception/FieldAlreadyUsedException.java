package com.openclassrooms.mddapi.exception;

import lombok.Getter;

/**
 * Thrown when a value that must be unique, such as an email address, is already used by another account. Each
 * unique field has its own subclass, all answered with a 409 naming the field by a single handler.
 */
@Getter
public abstract class FieldAlreadyUsedException extends RuntimeException {

	/** Name of the field in the request, so that the front-end can display the message next to it. */
	private final String field;

	protected FieldAlreadyUsedException(String field, String message) {
		super(message);
		this.field = field;
	}

}

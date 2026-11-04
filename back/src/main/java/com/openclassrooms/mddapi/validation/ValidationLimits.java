package com.openclassrooms.mddapi.validation;

/**
 * Lengths of the user input, shared by the request validation and the database columns so that both always agree.
 * The front-end applies the same limits to guide the user; the back-end remains the reference.
 */
public final class ValidationLimits {

	/** Longest usable email address (RFC 5321). */
	public static final int EMAIL_MAX = 254;

	public static final int USERNAME_MIN = 3;

	public static final int USERNAME_MAX = 30;

	public static final int PASSWORD_MIN = 8;

	/** OWASP recommendation, which also bounds the cost of hashing. */
	public static final int PASSWORD_MAX = 64;

	public static final int POST_TITLE_MAX = 150;

	public static final int POST_CONTENT_MAX = 10_000;

	public static final int COMMENT_MAX = 1000;

	private ValidationLimits() {
	}

}

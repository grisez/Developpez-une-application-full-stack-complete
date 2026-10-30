package com.openclassrooms.mddapi.util;

import java.util.Locale;

/**
 * Normal form of the email addresses, shared by every place that stores or searches one.
 */
public final class EmailAddresses {

	private EmailAddresses() {
	}

	/**
	 * Emails are case-insensitive in practice, so they are stored and searched in lowercase.
	 * Surrounding spaces are already removed by the request records.
	 *
	 * @param email the email as typed
	 * @return the email in its normal form
	 */
	public static String normalize(String email) {
		return email.toLowerCase(Locale.ROOT);
	}

}

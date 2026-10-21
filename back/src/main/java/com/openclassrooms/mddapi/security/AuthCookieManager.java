package com.openclassrooms.mddapi.security;

import java.time.Duration;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * Builds the cookie that carries the JWT between the browser and the API.
 * The cookie is HttpOnly so that JavaScript, and therefore an XSS attack, can never read the token.
 */
@Component
@RequiredArgsConstructor
public class AuthCookieManager {

	/** Name of the cookie holding the JWT. */
	public static final String COOKIE_NAME = "mdd_token";

	private static final String COOKIE_PATH = "/api";

	private final JwtProperties jwtProperties;

	/**
	 * Creates the cookie sent after a successful login or registration.
	 * It outlives the browser session so that the user stays logged in, as required by the specifications.
	 *
	 * @param token the signed JWT
	 * @return the authentication cookie
	 */
	public ResponseCookie createAuthCookie(String token) {
		return baseCookie(token).maxAge(jwtProperties.expiration()).build();
	}

	/**
	 * Creates an expired cookie that makes the browser delete the authentication cookie on logout.
	 *
	 * @return the clearing cookie
	 */
	public ResponseCookie clearAuthCookie() {
		return baseCookie("").maxAge(Duration.ZERO).build();
	}

	private ResponseCookie.ResponseCookieBuilder baseCookie(String value) {
		return ResponseCookie.from(COOKIE_NAME, value)
				.httpOnly(true)
				.secure(jwtProperties.cookieSecure())
				.sameSite("Strict")
				.path(COOKIE_PATH);
	}

}

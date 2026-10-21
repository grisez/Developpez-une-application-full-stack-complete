package com.openclassrooms.mddapi.security;

import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Reads the JWT from the authentication cookie instead of the default {@code Authorization} header,
 * since the front-end cannot read an HttpOnly cookie to copy it into a header.
 */
public class CookieBearerTokenResolver implements BearerTokenResolver {

	@Override
	public String resolve(HttpServletRequest request) {
		Cookie cookie = WebUtils.getCookie(request, AuthCookieManager.COOKIE_NAME);
		if (cookie == null || cookie.getValue().isBlank()) {
			return null;
		}
		return cookie.getValue();
	}

}

package com.openclassrooms.mddapi.security;

import java.util.Set;

import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Reads the JWT from the authentication cookie instead of the default {@code Authorization} header,
 * since the front-end cannot read an HttpOnly cookie to copy it into a header.
 */
public class CookieBearerTokenResolver implements BearerTokenResolver {

	private final Set<String> publicPaths;

	/**
	 * @param publicPaths routes reachable without being logged in. The cookie is ignored on them: an invalid token,
	 *                    for example after the signing key has changed, would otherwise be rejected before the route
	 *                    is reached, and the user could neither log in nor log out.
	 */
	public CookieBearerTokenResolver(Set<String> publicPaths) {
		this.publicPaths = publicPaths;
	}

	@Override
	public String resolve(HttpServletRequest request) {
		if (publicPaths.contains(UrlPathHelper.defaultInstance.getPathWithinApplication(request))) {
			return null;
		}
		Cookie cookie = WebUtils.getCookie(request, AuthCookieManager.COOKIE_NAME);
		if (cookie == null || cookie.getValue().isBlank()) {
			return null;
		}
		return cookie.getValue();
	}

}

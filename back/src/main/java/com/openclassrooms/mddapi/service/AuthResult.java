package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.UserResponse;

/**
 * Outcome of a successful registration or login: the user, and the token the controller puts in the cookie.
 * The token never appears in a response body.
 *
 * @param user  the authenticated user
 * @param token the signed JWT
 */
public record AuthResult(UserResponse user, String token) {
}

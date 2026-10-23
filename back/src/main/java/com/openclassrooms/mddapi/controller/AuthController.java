package com.openclassrooms.mddapi.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.mddapi.dto.LoginRequest;
import com.openclassrooms.mddapi.dto.RegisterRequest;
import com.openclassrooms.mddapi.dto.UserResponse;
import com.openclassrooms.mddapi.security.AuthCookieManager;
import com.openclassrooms.mddapi.service.AuthResult;
import com.openclassrooms.mddapi.service.AuthService;
import com.openclassrooms.mddapi.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST endpoints for authentication.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	private final UserService userService;

	private final AuthCookieManager authCookieManager;

	/**
	 * Creates an account and logs the new user in by setting the authentication cookie.
	 *
	 * @param request the registration data
	 * @return 201 with the created user
	 */
	@PostMapping("/register")
	public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
		AuthResult result = authService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.header(HttpHeaders.SET_COOKIE, authCookieManager.createAuthCookie(result.token()).toString())
				.body(result.user());
	}

	/**
	 * Logs a user in with their email or their username, and sets the authentication cookie.
	 *
	 * @param request the credentials
	 * @return 200 with the logged-in user
	 */
	@PostMapping("/login")
	public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {
		AuthResult result = authService.login(request);
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, authCookieManager.createAuthCookie(result.token()).toString())
				.body(result.user());
	}

	/**
	 * Logs the user out by making the browser delete the authentication cookie.
	 * A JWT cannot be revoked: a copy of the token made before logout stays valid until it expires.
	 *
	 * @return 204 with an expired authentication cookie
	 */
	@PostMapping("/logout")
	public ResponseEntity<Void> logout() {
		return ResponseEntity.noContent()
				.header(HttpHeaders.SET_COOKIE, authCookieManager.clearAuthCookie().toString())
				.build();
	}

	/**
	 * Returns the logged-in user. The front-end calls it to know whether a session is open,
	 * since it cannot read the HttpOnly cookie that holds the token.
	 *
	 * @param jwt the validated token of the current request
	 * @return the current user
	 */
	@GetMapping("/me")
	public UserResponse getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
		return userService.getById(Long.valueOf(jwt.getSubject()));
	}

}

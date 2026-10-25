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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST endpoints for authentication.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Registration, login, logout and current session")
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
	@SecurityRequirements
	@Operation(summary = "Create an account", description = "Logs the new user in by setting the authentication cookie.")
	@ApiResponse(responseCode = "201", description = "Account created, authentication cookie set")
	@ApiResponse(responseCode = "400", description = "Invalid fields, detailed in the errors property")
	@ApiResponse(responseCode = "403", description = "Missing or invalid CSRF token")
	@ApiResponse(responseCode = "409", description = "Email or username already used")
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
	@SecurityRequirements
	@Operation(summary = "Log in", description = "The identifier is either the email or the username.")
	@ApiResponse(responseCode = "200", description = "Logged in, authentication cookie set")
	@ApiResponse(responseCode = "400", description = "Missing identifier or password")
	@ApiResponse(responseCode = "401", description = "Invalid identifier or password, without telling which one")
	@ApiResponse(responseCode = "403", description = "Missing or invalid CSRF token")
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
	@SecurityRequirements
	@Operation(summary = "Log out", description = "Expires the authentication cookie, even if the token is no longer valid.")
	@ApiResponse(responseCode = "204", description = "Logged out, authentication cookie expired")
	@ApiResponse(responseCode = "403", description = "Missing or invalid CSRF token")
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
	@Operation(summary = "Get the logged-in user", description = "Used by the front-end to restore the session.")
	@ApiResponse(responseCode = "200", description = "The logged-in user, without password")
	@ApiResponse(responseCode = "401", description = "No valid session")
	public UserResponse getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
		return userService.getById(Long.valueOf(jwt.getSubject()));
	}

}

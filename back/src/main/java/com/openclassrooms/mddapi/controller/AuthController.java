package com.openclassrooms.mddapi.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.mddapi.dto.UserResponse;
import com.openclassrooms.mddapi.service.UserService;

import lombok.RequiredArgsConstructor;

/**
 * REST endpoints for authentication.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final UserService userService;

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

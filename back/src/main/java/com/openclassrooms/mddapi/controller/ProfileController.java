package com.openclassrooms.mddapi.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.mddapi.dto.ProfileResponse;
import com.openclassrooms.mddapi.dto.UpdateProfileRequest;
import com.openclassrooms.mddapi.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST endpoints for the profile of the logged-in user.
 */
@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class ProfileController {

	private final UserService userService;

	/**
	 * Returns the profile of the logged-in user, with their subscriptions.
	 *
	 * @param jwt the validated token of the current request
	 * @return the profile
	 */
	@GetMapping
	public ProfileResponse getProfile(@AuthenticationPrincipal Jwt jwt) {
		return userService.getProfile(Long.valueOf(jwt.getSubject()));
	}

	/**
	 * Updates the email, the username and, when given, the password of the logged-in user.
	 * The session stays valid: the token identifies the user by id, which never changes.
	 *
	 * @param jwt     the validated token of the current request
	 * @param request the changes
	 * @return the updated profile
	 */
	@PutMapping
	public ProfileResponse updateProfile(@AuthenticationPrincipal Jwt jwt,
			@Valid @RequestBody UpdateProfileRequest request) {
		return userService.updateProfile(Long.valueOf(jwt.getSubject()), request);
	}

}

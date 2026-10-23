package com.openclassrooms.mddapi.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.mddapi.dto.ProfileResponse;
import com.openclassrooms.mddapi.service.UserService;

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

}

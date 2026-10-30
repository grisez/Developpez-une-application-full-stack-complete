package com.openclassrooms.mddapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.mddapi.dto.ProfileResponse;
import com.openclassrooms.mddapi.dto.UpdateProfileRequest;
import com.openclassrooms.mddapi.security.CurrentUserId;
import com.openclassrooms.mddapi.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST endpoints for the profile of the logged-in user.
 */
@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "Profile of the logged-in user")
public class ProfileController {

	private final UserService userService;

	/**
	 * Returns the profile of the logged-in user, with their subscriptions.
	 *
	 * @param userId the id of the logged-in user
	 * @return the profile
	 */
	@GetMapping
	@Operation(summary = "Get my profile", description = "Email, username and subscriptions; never the password.")
	@ApiResponse(responseCode = "200", description = "The profile")
	@ApiResponse(responseCode = "401", description = "No valid session")
	public ProfileResponse getProfile(@CurrentUserId Long userId) {
		return userService.getProfile(userId);
	}

	/**
	 * Updates the email, the username and, when given, the password of the logged-in user.
	 * The session stays valid: the token identifies the user by id, which never changes.
	 *
	 * @param userId  the id of the logged-in user
	 * @param request the changes
	 * @return the updated profile
	 */
	@PutMapping
	@Operation(summary = "Update my profile",
			description = "Same rules as the registration; an empty password keeps the current one.")
	@ApiResponse(responseCode = "200", description = "The updated profile")
	@ApiResponse(responseCode = "400", description = "Invalid fields, detailed in the errors property")
	@ApiResponse(responseCode = "401", description = "No valid session")
	@ApiResponse(responseCode = "403", description = "Missing or invalid CSRF token")
	@ApiResponse(responseCode = "409", description = "Email or username used by another account")
	public ProfileResponse updateProfile(@CurrentUserId Long userId,
			@Valid @RequestBody UpdateProfileRequest request) {
		return userService.updateProfile(userId, request);
	}

}

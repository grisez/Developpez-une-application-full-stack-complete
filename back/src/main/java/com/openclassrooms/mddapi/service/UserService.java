package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.ProfileResponse;
import com.openclassrooms.mddapi.dto.UpdateProfileRequest;
import com.openclassrooms.mddapi.dto.UserResponse;

/**
 * Business operations on users.
 */
public interface UserService {

	/**
	 * Returns a user by id.
	 *
	 * @param id the user id
	 * @return the user
	 * @throws com.openclassrooms.mddapi.exception.UserNotFoundException if no user has this id
	 */
	UserResponse getById(Long id);

	/**
	 * Returns the profile of a user, with their subscriptions.
	 *
	 * @param id the user id
	 * @return the profile
	 * @throws com.openclassrooms.mddapi.exception.UserNotFoundException if no user has this id
	 */
	ProfileResponse getProfile(Long id);

	/**
	 * Updates the email, the username and, when given, the password of a user.
	 *
	 * @param id      the user id
	 * @param request the changes; a null password keeps the current one
	 * @return the updated profile
	 * @throws com.openclassrooms.mddapi.exception.EmailAlreadyUsedException    if another account uses the email
	 * @throws com.openclassrooms.mddapi.exception.UsernameAlreadyUsedException if another account uses the username
	 */
	ProfileResponse updateProfile(Long id, UpdateProfileRequest request);

}

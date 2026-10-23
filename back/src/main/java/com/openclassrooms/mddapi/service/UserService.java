package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.ProfileResponse;
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

}

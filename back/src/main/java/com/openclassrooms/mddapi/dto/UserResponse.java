package com.openclassrooms.mddapi.dto;

/**
 * Public view of a user returned by the API. The password hash is never part of it.
 *
 * @param id       the user id
 * @param username the unique username
 * @param email    the unique email address
 */
public record UserResponse(Long id, String username, String email) {
}

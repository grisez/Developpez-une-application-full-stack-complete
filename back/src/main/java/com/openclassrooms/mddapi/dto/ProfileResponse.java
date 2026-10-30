package com.openclassrooms.mddapi.dto;

import java.util.List;

/**
 * Profile of the logged-in user, as displayed on the profile page. The password hash is never part of it.
 *
 * @param id            the user id
 * @param username      the unique username
 * @param email         the unique email address
 * @param subscriptions the topics the user is subscribed to, sorted by name
 */
public record ProfileResponse(Long id, String username, String email, List<TopicResponse> subscriptions) {
}

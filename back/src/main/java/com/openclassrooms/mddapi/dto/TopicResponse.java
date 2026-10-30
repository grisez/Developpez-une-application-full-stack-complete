package com.openclassrooms.mddapi.dto;

/**
 * Topic as exposed by the API.
 *
 * @param id          topic identifier
 * @param name        topic name, unique
 * @param description short description displayed on the topics page
 * @param subscribed  whether the logged-in user is subscribed to the topic
 */
public record TopicResponse(Integer id, String name, String description, boolean subscribed) {
}

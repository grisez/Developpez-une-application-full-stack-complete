package com.openclassrooms.mddapi.dto;

/**
 * Topic as exposed by the API.
 *
 * @param id          topic identifier
 * @param name        topic name, unique
 * @param description short description displayed on the topics page
 */
public record TopicResponse(Integer id, String name, String description) {
}

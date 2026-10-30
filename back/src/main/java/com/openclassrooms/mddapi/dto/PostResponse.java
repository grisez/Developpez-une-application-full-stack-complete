package com.openclassrooms.mddapi.dto;

import java.time.Instant;

/**
 * Article as displayed in the news feed. "Article" in the user interface is "post" in the code,
 * following the glossary of the functional specifications.
 *
 * @param id             the post id
 * @param title          the title
 * @param content        the full content
 * @param createdAt      the publication date, set by the server
 * @param authorUsername the username of the author
 * @param topicId        the topic of the post
 * @param topicName      the name of the topic
 */
public record PostResponse(Long id, String title, String content, Instant createdAt, String authorUsername,
		Integer topicId, String topicName) {
}

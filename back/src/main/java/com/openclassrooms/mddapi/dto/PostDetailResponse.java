package com.openclassrooms.mddapi.dto;

import java.time.Instant;
import java.util.List;

/**
 * Article as displayed on its own page, with its comments.
 *
 * @param id             the post id
 * @param title          the title
 * @param content        the full content
 * @param createdAt      the publication date, set by the server
 * @param authorUsername the username of the author
 * @param topicId        the topic of the post
 * @param topicName      the name of the topic
 * @param comments       the comments, oldest first
 */
public record PostDetailResponse(Long id, String title, String content, Instant createdAt, String authorUsername,
		Integer topicId, String topicName, List<CommentResponse> comments) {
}

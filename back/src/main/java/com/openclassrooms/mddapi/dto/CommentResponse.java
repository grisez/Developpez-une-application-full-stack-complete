package com.openclassrooms.mddapi.dto;

import java.time.Instant;

/**
 * Comment as displayed under an article.
 *
 * @param id             the comment id
 * @param content        the text of the comment
 * @param createdAt      the publication date, set by the server
 * @param authorUsername the username of the author
 */
public record CommentResponse(Long id, String content, Instant createdAt, String authorUsername) {
}

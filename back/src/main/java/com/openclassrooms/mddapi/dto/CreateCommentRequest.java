package com.openclassrooms.mddapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data sent to comment on an article. The author and the date are never sent: the server sets them.
 *
 * @param content the text of the comment, at most 1,000 characters
 */
public record CreateCommentRequest(@NotBlank @Size(max = 1000) String content) {
}

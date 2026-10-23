package com.openclassrooms.mddapi.dto;

import org.springframework.util.StringUtils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data sent to publish an article. The author and the date are never sent: the server sets them.
 *
 * @param topicId the topic of the article
 * @param title   the title, at most 150 characters
 * @param content the content, at most 10,000 characters
 */
public record CreatePostRequest(
		@NotNull Integer topicId,
		@NotBlank @Size(max = 150) String title,
		@NotBlank @Size(max = 10_000) String content) {

	/**
	 * Removes the surrounding spaces of the title before validation runs. The content is kept as typed,
	 * since its line breaks and indentation are part of the article.
	 */
	public CreatePostRequest {
		title = StringUtils.trimWhitespace(title);
	}

}

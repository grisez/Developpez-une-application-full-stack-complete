package com.openclassrooms.mddapi.service;

import java.util.List;

import org.springframework.data.domain.Sort;

import com.openclassrooms.mddapi.dto.CreatePostRequest;
import com.openclassrooms.mddapi.dto.PostResponse;

/**
 * Business operations on posts.
 */
public interface PostService {

	/**
	 * Returns the news feed of a user: the posts of the topics they are subscribed to.
	 *
	 * @param userId    the logged-in user
	 * @param direction {@code DESC} for the most recent first, {@code ASC} for the oldest first
	 * @return the posts of the feed
	 */
	List<PostResponse> getFeed(Long userId, Sort.Direction direction);

	/**
	 * Publishes an article on behalf of a user; the publication date is set automatically.
	 *
	 * @param authorId the logged-in user, author of the article
	 * @param request  the topic, the title and the content
	 * @return the created article
	 * @throws com.openclassrooms.mddapi.exception.TopicNotFoundException if the topic does not exist
	 */
	PostResponse create(Long authorId, CreatePostRequest request);

}

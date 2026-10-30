package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.CommentResponse;
import com.openclassrooms.mddapi.dto.CreateCommentRequest;

/**
 * Business operations on comments.
 */
public interface CommentService {

	/**
	 * Adds a comment to an article on behalf of a user; the publication date is set automatically.
	 * A comment always belongs to an article, never to another comment.
	 *
	 * @param postId   the commented article
	 * @param authorId the logged-in user, author of the comment
	 * @param request  the text of the comment
	 * @return the created comment
	 * @throws com.openclassrooms.mddapi.exception.PostNotFoundException if the article does not exist
	 */
	CommentResponse create(Long postId, Long authorId, CreateCommentRequest request);

}

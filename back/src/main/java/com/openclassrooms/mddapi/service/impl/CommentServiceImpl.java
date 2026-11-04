package com.openclassrooms.mddapi.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.mddapi.dto.CommentResponse;
import com.openclassrooms.mddapi.dto.CreateCommentRequest;
import com.openclassrooms.mddapi.exception.PostNotFoundException;
import com.openclassrooms.mddapi.mapper.PostMapper;
import com.openclassrooms.mddapi.model.Comment;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.service.CommentService;

import lombok.RequiredArgsConstructor;

/**
 * Default implementation of {@link CommentService}, backed by the database.
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

	private final CommentRepository commentRepository;

	private final PostRepository postRepository;

	private final UserRepository userRepository;

	private final PostMapper postMapper;

	@Override
	@Transactional
	public CommentResponse create(Long postId, Long authorId, CreateCommentRequest request) {
		Comment comment = new Comment();
		comment.setContent(request.content());
		if (!postRepository.existsById(postId)) {
			throw new PostNotFoundException(postId);
		}
		// A reference is enough to set the foreign key: the article and its content are not loaded
		comment.setPost(postRepository.getReferenceById(postId));
		comment.setAuthor(userRepository.getExisting(authorId));
		// saveAndFlush writes the row now, so that the creation date set by Hibernate is in the response
		return postMapper.toResponse(commentRepository.saveAndFlush(comment));
	}

}

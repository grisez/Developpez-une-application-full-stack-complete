package com.openclassrooms.mddapi.service.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.mddapi.dto.CreatePostRequest;
import com.openclassrooms.mddapi.dto.PostResponse;
import com.openclassrooms.mddapi.exception.TopicNotFoundException;
import com.openclassrooms.mddapi.exception.UserNotFoundException;
import com.openclassrooms.mddapi.mapper.PostMapper;
import com.openclassrooms.mddapi.model.Post;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.service.PostService;

import lombok.RequiredArgsConstructor;

/**
 * Default implementation of {@link PostService}, backed by the database.
 */
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

	private final PostRepository postRepository;

	private final TopicRepository topicRepository;

	private final UserRepository userRepository;

	private final PostMapper postMapper;

	@Override
	@Transactional(readOnly = true)
	public List<PostResponse> getFeed(Long userId, Sort.Direction direction) {
		// The id breaks ties between posts published at the same instant, so that the order never changes
		Sort sort = Sort.by(direction, "createdAt").and(Sort.by(direction, "id"));
		return postRepository.findFeed(userId, sort).stream()
				.map(postMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional
	public PostResponse create(Long authorId, CreatePostRequest request) {
		Post post = new Post();
		post.setTitle(request.title());
		post.setContent(request.content());
		post.setTopic(topicRepository.findById(request.topicId())
				.orElseThrow(() -> new TopicNotFoundException(request.topicId())));
		post.setAuthor(userRepository.findById(authorId).orElseThrow(() -> new UserNotFoundException(authorId)));
		// saveAndFlush writes the row now, so that the creation date set by Hibernate is in the response
		return postMapper.toResponse(postRepository.saveAndFlush(post));
	}

}

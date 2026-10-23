package com.openclassrooms.mddapi.service.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.mddapi.dto.PostResponse;
import com.openclassrooms.mddapi.mapper.PostMapper;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.service.PostService;

import lombok.RequiredArgsConstructor;

/**
 * Default implementation of {@link PostService}, backed by the database.
 */
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

	private final PostRepository postRepository;

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

}

package com.openclassrooms.mddapi.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.mddapi.dto.TopicResponse;
import com.openclassrooms.mddapi.mapper.TopicMapper;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.service.TopicService;

import lombok.RequiredArgsConstructor;

/**
 * Default implementation of {@link TopicService}, backed by the database.
 */
@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

	private final TopicRepository topicRepository;

	private final TopicMapper topicMapper;

	@Override
	@Transactional(readOnly = true)
	public List<TopicResponse> getTopics() {
		return topicRepository.findAll().stream()
				.map(topicMapper::toResponse)
				.toList();
	}

}

package com.openclassrooms.mddapi.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.mddapi.dto.TopicResponse;
import com.openclassrooms.mddapi.mapper.TopicMapper;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.service.TopicService;

import lombok.RequiredArgsConstructor;

/**
 * Default implementation of {@link TopicService}, backed by the database.
 */
@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

	private final TopicRepository topicRepository;

	private final UserRepository userRepository;

	private final TopicMapper topicMapper;

	@Override
	@Transactional(readOnly = true)
	public List<TopicResponse> getTopics(Long userId) {
		Set<Integer> subscribedTopicIds = topicRepository.findSubscribedTopicIds(userId);
		return topicRepository.findAll(Sort.by("id")).stream()
				.map(topic -> topicMapper.toResponse(topic, subscribedTopicIds.contains(topic.getId())))
				.toList();
	}

	@Override
	@Transactional
	public void subscribe(Long userId, Integer topicId) {
		Topic topic = topicRepository.getExisting(topicId);
		// Subscriptions are a Set backed by a (user_id, topic_id) primary key: a second subscription adds nothing
		userRepository.getExisting(userId).getSubscriptions().add(topic);
	}

	@Override
	@Transactional
	public void unsubscribe(Long userId, Integer topicId) {
		Topic topic = topicRepository.getExisting(topicId);
		userRepository.getExisting(userId).getSubscriptions().remove(topic);
	}

}

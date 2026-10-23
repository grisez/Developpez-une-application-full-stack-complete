package com.openclassrooms.mddapi.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.mddapi.dto.TopicResponse;
import com.openclassrooms.mddapi.exception.TopicNotFoundException;
import com.openclassrooms.mddapi.exception.UserNotFoundException;
import com.openclassrooms.mddapi.mapper.TopicMapper;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
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
		Topic topic = findTopic(topicId);
		// Subscriptions are a Set backed by a (user_id, topic_id) primary key: a second subscription adds nothing
		findUser(userId).getSubscriptions().add(topic);
	}

	@Override
	@Transactional
	public void unsubscribe(Long userId, Integer topicId) {
		Topic topic = findTopic(topicId);
		findUser(userId).getSubscriptions().remove(topic);
	}

	private Topic findTopic(Integer topicId) {
		return topicRepository.findById(topicId).orElseThrow(() -> new TopicNotFoundException(topicId));
	}

	private User findUser(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
	}

}

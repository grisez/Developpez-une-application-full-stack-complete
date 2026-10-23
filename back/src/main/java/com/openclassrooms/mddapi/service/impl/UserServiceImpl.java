package com.openclassrooms.mddapi.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.mddapi.dto.ProfileResponse;
import com.openclassrooms.mddapi.dto.TopicResponse;
import com.openclassrooms.mddapi.dto.UserResponse;
import com.openclassrooms.mddapi.exception.UserNotFoundException;
import com.openclassrooms.mddapi.mapper.TopicMapper;
import com.openclassrooms.mddapi.mapper.UserMapper;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.service.UserService;

import lombok.RequiredArgsConstructor;

/**
 * Default implementation of {@link UserService}, backed by the database.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final TopicRepository topicRepository;

	private final UserMapper userMapper;

	private final TopicMapper topicMapper;

	@Override
	@Transactional(readOnly = true)
	public UserResponse getById(Long id) {
		return userMapper.toResponse(findUser(id));
	}

	@Override
	@Transactional(readOnly = true)
	public ProfileResponse getProfile(Long id) {
		User user = findUser(id);
		List<TopicResponse> subscriptions = topicRepository.findSubscribedTopics(id).stream()
				.map(topic -> topicMapper.toResponse(topic, true))
				.toList();
		return userMapper.toProfile(user, subscriptions);
	}

	private User findUser(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
	}

}

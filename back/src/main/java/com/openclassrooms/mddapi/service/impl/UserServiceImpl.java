package com.openclassrooms.mddapi.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.mddapi.dto.ProfileResponse;
import com.openclassrooms.mddapi.dto.TopicResponse;
import com.openclassrooms.mddapi.dto.UpdateProfileRequest;
import com.openclassrooms.mddapi.dto.UserResponse;
import com.openclassrooms.mddapi.exception.EmailAlreadyUsedException;
import com.openclassrooms.mddapi.exception.UserNotFoundException;
import com.openclassrooms.mddapi.exception.UsernameAlreadyUsedException;
import com.openclassrooms.mddapi.mapper.TopicMapper;
import com.openclassrooms.mddapi.mapper.UserMapper;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.service.UserService;
import com.openclassrooms.mddapi.util.EmailAddresses;

import lombok.RequiredArgsConstructor;

/**
 * Default implementation of {@link UserService}, backed by the database.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final TopicRepository topicRepository;

	private final PasswordEncoder passwordEncoder;

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
		return toProfile(findUser(id));
	}

	@Override
	@Transactional
	public ProfileResponse updateProfile(Long id, UpdateProfileRequest request) {
		User user = findUser(id);
		String email = EmailAddresses.normalize(request.email());
		if (userRepository.existsByEmailAndIdNot(email, id)) {
			throw new EmailAlreadyUsedException();
		}
		if (userRepository.existsByUsernameAndIdNot(request.username(), id)) {
			throw new UsernameAlreadyUsedException();
		}

		user.setEmail(email);
		user.setUsername(request.username());
		if (request.password() != null) {
			user.setPassword(passwordEncoder.encode(request.password()));
		}
		return toProfile(user);
	}

	private ProfileResponse toProfile(User user) {
		List<TopicResponse> subscriptions = topicRepository.findSubscribedTopics(user.getId()).stream()
				.map(topic -> topicMapper.toResponse(topic, true))
				.toList();
		return userMapper.toProfile(user, subscriptions);
	}

	private User findUser(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
	}

}

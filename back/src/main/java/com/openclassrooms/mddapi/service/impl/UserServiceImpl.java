package com.openclassrooms.mddapi.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.mddapi.dto.UserResponse;
import com.openclassrooms.mddapi.exception.UserNotFoundException;
import com.openclassrooms.mddapi.mapper.UserMapper;
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

	private final UserMapper userMapper;

	@Override
	@Transactional(readOnly = true)
	public UserResponse getById(Long id) {
		return userRepository.findById(id)
				.map(userMapper::toResponse)
				.orElseThrow(() -> new UserNotFoundException(id));
	}

}

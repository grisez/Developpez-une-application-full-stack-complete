package com.openclassrooms.mddapi.service.impl;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.mddapi.dto.RegisterRequest;
import com.openclassrooms.mddapi.exception.EmailAlreadyUsedException;
import com.openclassrooms.mddapi.exception.UsernameAlreadyUsedException;
import com.openclassrooms.mddapi.mapper.UserMapper;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.security.JwtService;
import com.openclassrooms.mddapi.service.AuthResult;
import com.openclassrooms.mddapi.service.AuthService;

import lombok.RequiredArgsConstructor;

/**
 * Default implementation of {@link AuthService}.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	private final JwtService jwtService;

	private final UserMapper userMapper;

	@Override
	@Transactional
	public AuthResult register(RegisterRequest request) {
		// Emails are case-insensitive in practice, so they are stored in a single form
		String email = request.email().trim().toLowerCase(Locale.ROOT);
		String username = request.username().trim();
		if (userRepository.existsByEmail(email)) {
			throw new EmailAlreadyUsedException();
		}
		if (userRepository.existsByUsername(username)) {
			throw new UsernameAlreadyUsedException();
		}

		User user = new User();
		user.setEmail(email);
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(request.password()));
		User savedUser = userRepository.save(user);

		return new AuthResult(userMapper.toResponse(savedUser), jwtService.generateToken(savedUser));
	}

}

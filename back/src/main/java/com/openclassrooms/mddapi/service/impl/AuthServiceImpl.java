package com.openclassrooms.mddapi.service.impl;

import java.util.Locale;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.mddapi.dto.LoginRequest;
import com.openclassrooms.mddapi.dto.RegisterRequest;
import com.openclassrooms.mddapi.exception.EmailAlreadyUsedException;
import com.openclassrooms.mddapi.exception.InvalidCredentialsException;
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
		String email = normalizeEmail(request.email());
		String username = request.username();
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

		return authenticated(savedUser);
	}

	@Override
	@Transactional(readOnly = true)
	public AuthResult login(LoginRequest request) {
		User user = findByIdentifier(request.identifier())
				.filter(candidate -> passwordEncoder.matches(request.password(), candidate.getPassword()))
				.orElseThrow(InvalidCredentialsException::new);
		return authenticated(user);
	}

	/**
	 * Usernames never contain {@code @}, so an identifier containing one can only be an email.
	 */
	private Optional<User> findByIdentifier(String identifier) {
		if (identifier.contains("@")) {
			return userRepository.findByEmail(normalizeEmail(identifier));
		}
		return userRepository.findByUsername(identifier);
	}

	private AuthResult authenticated(User user) {
		return new AuthResult(userMapper.toResponse(user), jwtService.generateToken(user));
	}

	/**
	 * Emails are case-insensitive in practice, so they are stored and searched in a single form.
	 * Surrounding spaces are already removed by the request records.
	 */
	private static String normalizeEmail(String email) {
		return email.toLowerCase(Locale.ROOT);
	}

}

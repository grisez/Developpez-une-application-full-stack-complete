package com.openclassrooms.mddapi.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.openclassrooms.mddapi.dto.RegisterRequest;
import com.openclassrooms.mddapi.dto.UserResponse;
import com.openclassrooms.mddapi.exception.EmailAlreadyUsedException;
import com.openclassrooms.mddapi.exception.UsernameAlreadyUsedException;
import com.openclassrooms.mddapi.mapper.UserMapper;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.security.JwtService;
import com.openclassrooms.mddapi.service.AuthResult;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtService jwtService;

	private AuthServiceImpl authService;

	@BeforeEach
	void setUp() {
		authService = new AuthServiceImpl(userRepository, passwordEncoder, jwtService,
				Mappers.getMapper(UserMapper.class));
	}

	@Test
	void registerStoresTheNormalizedUserWithAHashedPasswordAndReturnsAToken() {
		// Arrange
		when(passwordEncoder.encode("Str0ng!Password")).thenReturn("$argon2id$hash");
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
			User user = invocation.getArgument(0);
			user.setId(1L);
			return user;
		});
		when(jwtService.generateToken(any(User.class))).thenReturn("signed-token");

		// Act
		AuthResult result = authService.register(
				new RegisterRequest("  Alice@MDD.dev ", " alice ", "Str0ng!Password"));

		// Assert
		ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(savedUser.capture());
		assertThat(savedUser.getValue().getEmail()).isEqualTo("alice@mdd.dev");
		assertThat(savedUser.getValue().getUsername()).isEqualTo("alice");
		assertThat(savedUser.getValue().getPassword()).isEqualTo("$argon2id$hash");
		assertThat(result.user()).isEqualTo(new UserResponse(1L, "alice", "alice@mdd.dev"));
		assertThat(result.token()).isEqualTo("signed-token");
	}

	@Test
	void registerRejectsAnEmailAlreadyUsed() {
		// Arrange
		when(userRepository.existsByEmail("alice@mdd.dev")).thenReturn(true);

		// Act and assert
		assertThatThrownBy(() -> authService.register(new RegisterRequest("alice@mdd.dev", "alice", "Str0ng!Password")))
				.isInstanceOf(EmailAlreadyUsedException.class);
		verify(userRepository, never()).save(any());
	}

	@Test
	void registerRejectsAUsernameAlreadyUsed() {
		// Arrange
		when(userRepository.existsByUsername("alice")).thenReturn(true);

		// Act and assert
		assertThatThrownBy(() -> authService.register(new RegisterRequest("alice@mdd.dev", "alice", "Str0ng!Password")))
				.isInstanceOf(UsernameAlreadyUsedException.class);
		verify(userRepository, never()).save(any());
	}

}

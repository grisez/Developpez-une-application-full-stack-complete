package com.openclassrooms.mddapi.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.mddapi.dto.UserResponse;
import com.openclassrooms.mddapi.exception.UserNotFoundException;
import com.openclassrooms.mddapi.mapper.UserMapper;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	private UserServiceImpl userService;

	@BeforeEach
	void setUp() {
		userService = new UserServiceImpl(userRepository, Mappers.getMapper(UserMapper.class));
	}

	@Test
	void getByIdReturnsTheUserWithoutItsPassword() {
		// Arrange
		User user = new User();
		user.setId(3L);
		user.setUsername("bob");
		user.setEmail("bob@mdd.dev");
		user.setPassword("$argon2id$hash");
		when(userRepository.findById(3L)).thenReturn(Optional.of(user));

		// Act
		UserResponse response = userService.getById(3L);

		// Assert
		assertThat(response).isEqualTo(new UserResponse(3L, "bob", "bob@mdd.dev"));
	}

	@Test
	void getByIdThrowsWhenTheUserDoesNotExist() {
		// Arrange
		when(userRepository.findById(99L)).thenReturn(Optional.empty());

		// Act and assert
		assertThatThrownBy(() -> userService.getById(99L)).isInstanceOf(UserNotFoundException.class);
	}

}

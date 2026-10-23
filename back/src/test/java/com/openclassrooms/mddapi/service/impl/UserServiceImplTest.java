package com.openclassrooms.mddapi.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.mddapi.dto.ProfileResponse;
import com.openclassrooms.mddapi.dto.TopicResponse;
import com.openclassrooms.mddapi.dto.UserResponse;
import com.openclassrooms.mddapi.exception.UserNotFoundException;
import com.openclassrooms.mddapi.mapper.TopicMapper;
import com.openclassrooms.mddapi.mapper.UserMapper;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private TopicRepository topicRepository;

	private UserServiceImpl userService;

	@BeforeEach
	void setUp() {
		userService = new UserServiceImpl(userRepository, topicRepository, Mappers.getMapper(UserMapper.class),
				Mappers.getMapper(TopicMapper.class));
	}

	@Test
	void getByIdReturnsTheUserWithoutItsPassword() {
		// Arrange
		when(userRepository.findById(3L)).thenReturn(Optional.of(bob()));

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

	@Test
	void getProfileReturnsTheUserWithTheirSubscriptions() {
		// Arrange
		Topic java = new Topic();
		java.setId(1);
		java.setName("Java");
		java.setDescription("The JVM ecosystem");
		when(userRepository.findById(3L)).thenReturn(Optional.of(bob()));
		when(topicRepository.findSubscribedTopics(3L)).thenReturn(List.of(java));

		// Act
		ProfileResponse profile = userService.getProfile(3L);

		// Assert
		assertThat(profile).isEqualTo(new ProfileResponse(3L, "bob", "bob@mdd.dev",
				List.of(new TopicResponse(1, "Java", "The JVM ecosystem", true))));
	}

	private static User bob() {
		User user = new User();
		user.setId(3L);
		user.setUsername("bob");
		user.setEmail("bob@mdd.dev");
		user.setPassword("$argon2id$hash");
		return user;
	}

}

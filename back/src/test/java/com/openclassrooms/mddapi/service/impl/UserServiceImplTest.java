package com.openclassrooms.mddapi.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.openclassrooms.mddapi.dto.ProfileResponse;
import com.openclassrooms.mddapi.dto.TopicResponse;
import com.openclassrooms.mddapi.dto.UpdateProfileRequest;
import com.openclassrooms.mddapi.dto.UserResponse;
import com.openclassrooms.mddapi.exception.EmailAlreadyUsedException;
import com.openclassrooms.mddapi.exception.UserNotFoundException;
import com.openclassrooms.mddapi.exception.UsernameAlreadyUsedException;
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

	@Mock
	private PasswordEncoder passwordEncoder;

	private UserServiceImpl userService;

	@BeforeEach
	void setUp() {
		userService = new UserServiceImpl(userRepository, topicRepository, passwordEncoder,
				Mappers.getMapper(UserMapper.class), Mappers.getMapper(TopicMapper.class));
	}

	@Test
	void updateProfileChangesEmailUsernameAndHashesTheNewPassword() {
		// Arrange
		User bob = bob();
		when(userRepository.findById(3L)).thenReturn(Optional.of(bob));
		when(passwordEncoder.encode("N3w!Password")).thenReturn("$argon2id$new-hash");

		// Act
		ProfileResponse profile = userService.updateProfile(3L,
				new UpdateProfileRequest("Robert@MDD.dev", "robert", "N3w!Password"));

		// Assert
		assertThat(profile.username()).isEqualTo("robert");
		assertThat(profile.email()).isEqualTo("robert@mdd.dev");
		assertThat(bob.getPassword()).isEqualTo("$argon2id$new-hash");
	}

	@Test
	void updateProfileKeepsThePasswordWhenNoneIsGiven() {
		// Arrange
		User bob = bob();
		when(userRepository.findById(3L)).thenReturn(Optional.of(bob));

		// Act
		userService.updateProfile(3L, new UpdateProfileRequest("bob@mdd.dev", "bob", ""));

		// Assert
		assertThat(bob.getPassword()).isEqualTo("$argon2id$hash");
		verify(passwordEncoder, never()).encode(any());
	}

	@Test
	void updateProfileRejectsAnEmailUsedByAnotherAccount() {
		// Arrange
		User bob = bob();
		when(userRepository.findById(3L)).thenReturn(Optional.of(bob));
		when(userRepository.existsByEmailAndIdNot("alice@mdd.dev", 3L)).thenReturn(true);

		// Act and assert
		assertThatThrownBy(() -> userService.updateProfile(3L, new UpdateProfileRequest("alice@mdd.dev", "bob", null)))
				.isInstanceOf(EmailAlreadyUsedException.class);
		assertThat(bob.getEmail()).isEqualTo("bob@mdd.dev");
	}

	@Test
	void updateProfileRejectsAUsernameUsedByAnotherAccount() {
		// Arrange
		when(userRepository.findById(3L)).thenReturn(Optional.of(bob()));
		when(userRepository.existsByUsernameAndIdNot("alice", 3L)).thenReturn(true);

		// Act and assert
		assertThatThrownBy(() -> userService.updateProfile(3L, new UpdateProfileRequest("bob@mdd.dev", "alice", null)))
				.isInstanceOf(UsernameAlreadyUsedException.class);
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

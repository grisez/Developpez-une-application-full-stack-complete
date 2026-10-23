package com.openclassrooms.mddapi.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.openclassrooms.mddapi.dto.CreatePostRequest;
import com.openclassrooms.mddapi.dto.PostResponse;
import com.openclassrooms.mddapi.exception.TopicNotFoundException;
import com.openclassrooms.mddapi.mapper.PostMapper;
import com.openclassrooms.mddapi.model.Post;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

	@Mock
	private PostRepository postRepository;

	@Mock
	private TopicRepository topicRepository;

	@Mock
	private UserRepository userRepository;

	private PostServiceImpl postService;

	@BeforeEach
	void setUp() {
		postService = new PostServiceImpl(postRepository, topicRepository, userRepository,
				Mappers.getMapper(PostMapper.class));
	}

	@Test
	void createPublishesThePostOnTheTopicWithTheLoggedInUserAsAuthor() {
		// Arrange
		Post existing = post(Instant.parse("2026-10-20T10:00:00Z"));
		when(topicRepository.findById(2)).thenReturn(Optional.of(existing.getTopic()));
		when(userRepository.findById(7L)).thenReturn(Optional.of(existing.getAuthor()));
		when(postRepository.saveAndFlush(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// Act
		PostResponse created = postService.create(7L, new CreatePostRequest(2, "Hello", "First post"));

		// Assert
		ArgumentCaptor<Post> saved = ArgumentCaptor.forClass(Post.class);
		verify(postRepository).saveAndFlush(saved.capture());
		assertThat(saved.getValue().getAuthor().getUsername()).isEqualTo("alice");
		assertThat(saved.getValue().getTopic().getId()).isEqualTo(2);
		assertThat(created.title()).isEqualTo("Hello");
	}

	@Test
	void createRejectsAnUnknownTopic() {
		// Arrange
		when(topicRepository.findById(99)).thenReturn(Optional.empty());

		// Act and assert
		assertThatThrownBy(() -> postService.create(7L, new CreatePostRequest(99, "Hello", "First post")))
				.isInstanceOf(TopicNotFoundException.class);
		verify(postRepository, never()).saveAndFlush(any());
	}

	@Test
	void getFeedReturnsThePostsInTheRequestedOrderWithTheirAuthorAndTopic() {
		// Arrange
		Instant publishedAt = Instant.parse("2026-10-20T10:00:00Z");
		Sort oldestFirst = Sort.by(Sort.Direction.ASC, "createdAt").and(Sort.by(Sort.Direction.ASC, "id"));
		when(postRepository.findFeed(eq(7L), eq(oldestFirst))).thenReturn(List.of(post(publishedAt)));

		// Act
		List<PostResponse> feed = postService.getFeed(7L, Sort.Direction.ASC);

		// Assert
		assertThat(feed).containsExactly(
				new PostResponse(1L, "Hello", "First post", publishedAt, "alice", 2, "Java"));
	}

	private static Post post(Instant createdAt) {
		User author = new User();
		author.setUsername("alice");
		Topic topic = new Topic();
		topic.setId(2);
		topic.setName("Java");
		Post post = new Post();
		post.setId(1L);
		post.setTitle("Hello");
		post.setContent("First post");
		post.setCreatedAt(createdAt);
		post.setAuthor(author);
		post.setTopic(topic);
		return post;
	}

}

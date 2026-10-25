package com.openclassrooms.mddapi.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.mddapi.dto.CommentResponse;
import com.openclassrooms.mddapi.dto.CreateCommentRequest;
import com.openclassrooms.mddapi.exception.PostNotFoundException;
import com.openclassrooms.mddapi.mapper.PostMapper;
import com.openclassrooms.mddapi.model.Comment;
import com.openclassrooms.mddapi.model.Post;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private PostRepository postRepository;

	@Mock
	private UserRepository userRepository;

	private CommentServiceImpl commentService;

	@BeforeEach
	void setUp() {
		commentService = new CommentServiceImpl(commentRepository, postRepository, userRepository,
				Mappers.getMapper(PostMapper.class));
	}

	@Test
	void createAttachesTheCommentToTheArticleWithTheLoggedInUserAsAuthor() {
		// Arrange
		Post post = new Post();
		post.setId(5L);
		User bob = new User();
		bob.setUsername("bob");
		when(postRepository.findById(5L)).thenReturn(Optional.of(post));
		when(userRepository.findById(7L)).thenReturn(Optional.of(bob));
		when(commentRepository.saveAndFlush(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// Act
		CommentResponse created = commentService.create(5L, 7L, new CreateCommentRequest("Nice post"));

		// Assert
		ArgumentCaptor<Comment> saved = ArgumentCaptor.forClass(Comment.class);
		verify(commentRepository).saveAndFlush(saved.capture());
		assertThat(saved.getValue().getPost()).isSameAs(post);
		assertThat(created.authorUsername()).isEqualTo("bob");
		assertThat(created.content()).isEqualTo("Nice post");
	}

	@Test
	void createRejectsAnUnknownArticle() {
		// Arrange
		when(postRepository.findById(99L)).thenReturn(Optional.empty());

		// Act and assert
		assertThatThrownBy(() -> commentService.create(99L, 7L, new CreateCommentRequest("Nice post")))
				.isInstanceOf(PostNotFoundException.class);
		verify(commentRepository, never()).saveAndFlush(any());
	}

}

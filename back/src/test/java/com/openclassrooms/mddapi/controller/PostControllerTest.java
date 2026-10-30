package com.openclassrooms.mddapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.openclassrooms.mddapi.dto.CommentResponse;
import com.openclassrooms.mddapi.dto.CreatePostRequest;
import com.openclassrooms.mddapi.dto.PostDetailResponse;
import com.openclassrooms.mddapi.dto.PostResponse;
import com.openclassrooms.mddapi.exception.PostNotFoundException;
import com.openclassrooms.mddapi.exception.TopicNotFoundException;
import com.openclassrooms.mddapi.security.SecurityConfig;
import com.openclassrooms.mddapi.service.PostService;

@WebMvcTest(PostController.class)
@Import(SecurityConfig.class)
class PostControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private PostService postService;

	@Test
	void getFeedReturnsTheMostRecentPostsFirstByDefault() throws Exception {
		// Arrange
		when(postService.getFeed(7L, Sort.Direction.DESC)).thenReturn(List.of(
				new PostResponse(1L, "Hello", "First post", Instant.parse("2026-10-20T10:00:00Z"), "alice", 2, "Java")));

		// Act and assert
		mockMvc.perform(get("/api/articles").with(jwt().jwt(token -> token.subject("7"))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].title").value("Hello"))
				.andExpect(jsonPath("$[0].authorUsername").value("alice"))
				.andExpect(jsonPath("$[0].topicName").value("Java"))
				.andExpect(jsonPath("$[0].createdAt").value("2026-10-20T10:00:00Z"));
	}

	@Test
	void getFeedCanListTheOldestPostsFirst() throws Exception {
		// Act
		mockMvc.perform(get("/api/articles").param("sort", "asc").with(jwt().jwt(token -> token.subject("7"))))
				.andExpect(status().isOk());

		// Assert
		verify(postService).getFeed(7L, Sort.Direction.ASC);
	}

	@Test
	void getByIdReturnsTheArticleWithItsComments() throws Exception {
		// Arrange
		when(postService.getById(5L)).thenReturn(new PostDetailResponse(5L, "Hello", "First post",
				Instant.parse("2026-10-20T10:00:00Z"), "alice", 2, "Java",
				List.of(new CommentResponse(9L, "Nice post", Instant.parse("2026-10-21T08:00:00Z"), "bob"))));

		// Act and assert
		mockMvc.perform(get("/api/articles/5").with(jwt().jwt(token -> token.subject("7"))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Hello"))
				.andExpect(jsonPath("$.topicName").value("Java"))
				.andExpect(jsonPath("$.comments[0].content").value("Nice post"))
				.andExpect(jsonPath("$.comments[0].authorUsername").value("bob"));
	}

	@Test
	void getByIdReturnsNotFoundForAnUnknownArticle() throws Exception {
		// Arrange
		when(postService.getById(99L)).thenThrow(new PostNotFoundException(99L));

		// Act and assert
		mockMvc.perform(get("/api/articles/99").with(jwt().jwt(token -> token.subject("7"))))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.detail").value("Article not found."));
	}

	@Test
	void createReturnsTheCreatedPostWithItsLocation() throws Exception {
		// Arrange
		when(postService.create(eq(7L), any(CreatePostRequest.class))).thenReturn(
				new PostResponse(5L, "Hello", "First post", Instant.parse("2026-10-23T10:00:00Z"), "alice", 2, "Java"));

		// Act and assert
		mockMvc.perform(create("""
						{"topicId": 2, "title": "Hello", "content": "First post"}"""))
				.andExpect(status().isCreated())
				.andExpect(header().string(HttpHeaders.LOCATION, "/api/articles/5"))
				.andExpect(jsonPath("$.id").value(5));
	}

	@Test
	void createRequiresATopicATitleAndAContent() throws Exception {
		// Act and assert
		mockMvc.perform(create("""
						{"title": "  ", "content": ""}"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors.topicId").exists())
				.andExpect(jsonPath("$.errors.title").exists())
				.andExpect(jsonPath("$.errors.content").exists());
		verifyNoInteractions(postService);
	}

	@Test
	void createOnAnUnknownTopicReturnsNotFound() throws Exception {
		// Arrange
		when(postService.create(eq(7L), any(CreatePostRequest.class))).thenThrow(new TopicNotFoundException(99));

		// Act and assert
		mockMvc.perform(create("""
						{"topicId": 99, "title": "Hello", "content": "First post"}"""))
				.andExpect(status().isNotFound());
	}

	private static MockHttpServletRequestBuilder create(String json) {
		return post("/api/articles")
				.with(jwt().jwt(token -> token.subject("7")))
				.contentType(MediaType.APPLICATION_JSON)
				.content(json);
	}

	@Test
	void getFeedRejectsAnUnknownSortOrder() throws Exception {
		// Act and assert
		mockMvc.perform(get("/api/articles").param("sort", "random").with(jwt().jwt(token -> token.subject("7"))))
				.andExpect(status().isBadRequest());
		verifyNoInteractions(postService);
	}

}

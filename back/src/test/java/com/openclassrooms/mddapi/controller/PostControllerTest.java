package com.openclassrooms.mddapi.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.mddapi.dto.PostResponse;
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
	void getFeedRejectsAnUnknownSortOrder() throws Exception {
		// Act and assert
		mockMvc.perform(get("/api/articles").param("sort", "random").with(jwt().jwt(token -> token.subject("7"))))
				.andExpect(status().isBadRequest());
		verifyNoInteractions(postService);
	}

}

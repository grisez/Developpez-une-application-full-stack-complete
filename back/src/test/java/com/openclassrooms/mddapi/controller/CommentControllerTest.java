package com.openclassrooms.mddapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.openclassrooms.mddapi.dto.CommentResponse;
import com.openclassrooms.mddapi.dto.CreateCommentRequest;
import com.openclassrooms.mddapi.exception.PostNotFoundException;
import com.openclassrooms.mddapi.security.SecurityConfig;
import com.openclassrooms.mddapi.service.CommentService;

@WebMvcTest(CommentController.class)
@Import(SecurityConfig.class)
class CommentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CommentService commentService;

	@Test
	void createReturnsTheCreatedComment() throws Exception {
		// Arrange
		when(commentService.create(eq(5L), eq(7L), any(CreateCommentRequest.class)))
				.thenReturn(new CommentResponse(9L, "Nice post", Instant.parse("2026-10-24T10:00:00Z"), "bob"));

		// Act and assert
		mockMvc.perform(comment(5, """
						{"content": "Nice post"}"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.authorUsername").value("bob"))
				.andExpect(jsonPath("$.content").value("Nice post"));
	}

	@Test
	void createRequiresAContentOfAtMost1000Characters() throws Exception {
		// Act and assert
		mockMvc.perform(comment(5, """
						{"content": "   "}"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors.content").exists());
		mockMvc.perform(comment(5, """
						{"content": "%s"}""".formatted("a".repeat(1001))))
				.andExpect(status().isBadRequest());
		verifyNoInteractions(commentService);
	}

	@Test
	void createOnAnUnknownArticleReturnsNotFound() throws Exception {
		// Arrange
		when(commentService.create(eq(99L), eq(7L), any(CreateCommentRequest.class)))
				.thenThrow(new PostNotFoundException(99L));

		// Act and assert
		mockMvc.perform(comment(99, """
						{"content": "Nice post"}"""))
				.andExpect(status().isNotFound());
	}

	private static MockHttpServletRequestBuilder comment(long postId, String json) {
		return post("/api/articles/{postId}/comments", postId)
				.with(jwt().jwt(token -> token.subject("7")))
				.contentType(MediaType.APPLICATION_JSON)
				.content(json);
	}

}

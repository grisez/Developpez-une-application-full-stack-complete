package com.openclassrooms.mddapi.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.openclassrooms.mddapi.dto.TopicResponse;
import com.openclassrooms.mddapi.exception.TopicNotFoundException;
import com.openclassrooms.mddapi.security.SecurityConfig;
import com.openclassrooms.mddapi.service.TopicService;

import jakarta.servlet.http.Cookie;

@WebMvcTest(TopicController.class)
@Import(SecurityConfig.class)
class TopicControllerTest {

	@Autowired
	private MockMvc mockMvc;

	private static final String CSRF_TOKEN = "a-csrf-token-value";

	@MockitoBean
	private TopicService topicService;

	@Test
	void subscribeFollowsTheTopicForTheLoggedInUser() throws Exception {
		// Act and assert
		mockMvc.perform(subscribe(2))
				.andExpect(status().isNoContent());
		verify(topicService).subscribe(7L, 2);
	}

	@Test
	void subscribeToAnUnknownTopicReturnsNotFound() throws Exception {
		// Arrange
		doThrow(new TopicNotFoundException(99)).when(topicService).subscribe(7L, 99);

		// Act and assert
		mockMvc.perform(subscribe(99))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.detail").value("Topic not found."));
	}

	/**
	 * A subscription request of user 7, carrying the CSRF cookie and header as Angular sends them.
	 * The CSRF check itself is tested with real token cookies in TopicIntegrationTest and SecurityConfigTest:
	 * the jwt() post-processor of spring-security-test disables it on the simulated request.
	 */
	private static MockHttpServletRequestBuilder subscribe(int topicId) {
		return post("/api/topics/{id}/subscribe", topicId)
				.with(jwt().jwt(token -> token.subject("7")))
				.cookie(new Cookie("XSRF-TOKEN", CSRF_TOKEN))
				.header("X-XSRF-TOKEN", CSRF_TOKEN);
	}

	@Test
	void getTopicsReturnsTheTopicsOfTheLoggedInUserAsJson() throws Exception {
		// Arrange
		when(topicService.getTopics(7L))
				.thenReturn(List.of(new TopicResponse(1, "Java", "The JVM ecosystem", true)));

		// Act and assert
		mockMvc.perform(get("/api/topics").with(jwt().jwt(token -> token.subject("7"))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].name").value("Java"))
				.andExpect(jsonPath("$[0].description").value("The JVM ecosystem"))
				.andExpect(jsonPath("$[0].subscribed").value(true));
	}

	@Test
	void unexpectedErrorReturnsGenericProblemWithoutInternalDetails() throws Exception {
		// Arrange
		when(topicService.getTopics(7L)).thenThrow(new IllegalStateException("database password is wrong"));

		// Act and assert
		mockMvc.perform(get("/api/topics").with(jwt().jwt(token -> token.subject("7"))))
				.andExpect(status().isInternalServerError())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
				.andExpect(jsonPath("$.detail").value("An unexpected error occurred."))
				.andExpect(content().string(not(containsString("password"))));
	}

	@Test
	void unknownRouteReturnsNotFoundProblem() throws Exception {
		// Act and assert
		mockMvc.perform(get("/api/unknown").with(jwt()))
				.andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
	}

}

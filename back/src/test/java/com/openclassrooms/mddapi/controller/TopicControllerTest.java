package com.openclassrooms.mddapi.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import com.openclassrooms.mddapi.dto.TopicResponse;
import com.openclassrooms.mddapi.security.SecurityConfig;
import com.openclassrooms.mddapi.service.TopicService;

@WebMvcTest(TopicController.class)
@Import(SecurityConfig.class)
class TopicControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private TopicService topicService;

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

package com.openclassrooms.mddapi.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.mddapi.dto.ProfileResponse;
import com.openclassrooms.mddapi.dto.TopicResponse;
import com.openclassrooms.mddapi.security.SecurityConfig;
import com.openclassrooms.mddapi.service.UserService;

@WebMvcTest(ProfileController.class)
@Import(SecurityConfig.class)
class ProfileControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	@Test
	void getProfileReturnsTheLoggedInUserWithTheirSubscriptionsButNoPassword() throws Exception {
		// Arrange
		when(userService.getProfile(7L)).thenReturn(new ProfileResponse(7L, "alice", "alice@mdd.dev",
				List.of(new TopicResponse(1, "Java", "The JVM ecosystem", true))));

		// Act and assert
		mockMvc.perform(get("/api/me").with(jwt().jwt(token -> token.subject("7"))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("alice"))
				.andExpect(jsonPath("$.email").value("alice@mdd.dev"))
				.andExpect(jsonPath("$.subscriptions[0].name").value("Java"))
				.andExpect(jsonPath("$.password").doesNotExist());
	}

	@Test
	void getProfileRequiresAuthentication() throws Exception {
		// Act and assert
		mockMvc.perform(get("/api/me"))
				.andExpect(status().isUnauthorized());
	}

}

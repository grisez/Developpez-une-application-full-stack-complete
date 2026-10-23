package com.openclassrooms.mddapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.openclassrooms.mddapi.dto.ProfileResponse;
import com.openclassrooms.mddapi.dto.TopicResponse;
import com.openclassrooms.mddapi.dto.UpdateProfileRequest;
import com.openclassrooms.mddapi.exception.UsernameAlreadyUsedException;
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

	@Test
	void updateProfileReturnsTheUpdatedProfile() throws Exception {
		// Arrange
		when(userService.updateProfile(eq(7L), any(UpdateProfileRequest.class)))
				.thenReturn(new ProfileResponse(7L, "alicia", "alicia@mdd.dev", List.of()));

		// Act and assert
		mockMvc.perform(update("""
						{"email": "alicia@mdd.dev", "username": "alicia", "password": ""}"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("alicia"));
	}

	@Test
	void updateProfileRejectsAWeakNewPassword() throws Exception {
		// Act and assert
		mockMvc.perform(update("""
						{"email": "alice@mdd.dev", "username": "alice", "password": "weak"}"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors.password").exists());
	}

	@Test
	void updateProfileReturnsConflictWhenTheUsernameIsTaken() throws Exception {
		// Arrange
		when(userService.updateProfile(eq(7L), any(UpdateProfileRequest.class)))
				.thenThrow(new UsernameAlreadyUsedException());

		// Act and assert
		mockMvc.perform(update("""
						{"email": "alice@mdd.dev", "username": "bob"}"""))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.errors.username").exists());
	}

	private static MockHttpServletRequestBuilder update(String json) {
		return put("/api/me")
				.with(jwt().jwt(token -> token.subject("7")))
				.contentType(MediaType.APPLICATION_JSON)
				.content(json);
	}

}

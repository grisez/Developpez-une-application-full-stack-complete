package com.openclassrooms.mddapi.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.mddapi.dto.UserResponse;
import com.openclassrooms.mddapi.security.SecurityConfig;
import com.openclassrooms.mddapi.service.UserService;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	@Test
	void getCurrentUserReturnsTheUserIdentifiedByTheToken() throws Exception {
		// Arrange
		when(userService.getById(7L)).thenReturn(new UserResponse(7L, "alice", "alice@mdd.dev"));

		// Act and assert
		mockMvc.perform(get("/api/auth/me").with(jwt().jwt(token -> token.subject("7"))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(7))
				.andExpect(jsonPath("$.username").value("alice"))
				.andExpect(jsonPath("$.email").value("alice@mdd.dev"))
				.andExpect(jsonPath("$.password").doesNotExist());
	}

	@Test
	void getCurrentUserRequiresAuthentication() throws Exception {
		// Act and assert
		mockMvc.perform(get("/api/auth/me"))
				.andExpect(status().isUnauthorized());
	}

}

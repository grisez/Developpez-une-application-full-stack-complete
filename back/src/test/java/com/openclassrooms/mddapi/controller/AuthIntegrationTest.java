package com.openclassrooms.mddapi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.openclassrooms.mddapi.TestcontainersConfiguration;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.security.AuthCookieManager;

import jakarta.servlet.http.Cookie;

/**
 * Registration through the whole application, down to a real MySQL database.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class AuthIntegrationTest {

	private static final String CSRF_TOKEN = "a-csrf-token-value";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Test
	void registeredUserIsLoggedInAndStoredWithAnArgon2idHash() throws Exception {
		// Act
		Cookie authCookie = mockMvc.perform(register("Erin@MDD.dev", "erin"))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getCookie(AuthCookieManager.COOKIE_NAME);

		// Assert
		assertThat(authCookie).isNotNull();
		mockMvc.perform(get("/api/auth/me").cookie(authCookie))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("erin"))
				.andExpect(jsonPath("$.email").value("erin@mdd.dev"));
		assertThat(userRepository.findAll())
				.filteredOn(user -> "erin".equals(user.getUsername()))
				.singleElement()
				.satisfies(user -> assertThat(user.getPassword()).startsWith("$argon2id$"));
	}

	@Test
	void registeringTwiceWithTheSameEmailInAnotherCaseIsRejected() throws Exception {
		// Arrange
		mockMvc.perform(register("frank@mdd.dev", "frank")).andExpect(status().isCreated());

		// Act and assert
		mockMvc.perform(register("FRANK@mdd.dev", "frank2"))
				.andExpect(status().isConflict());
	}

	private static MockHttpServletRequestBuilder register(String email, String username) {
		return post("/api/auth/register")
				.cookie(new Cookie("XSRF-TOKEN", CSRF_TOKEN))
				.header("X-XSRF-TOKEN", CSRF_TOKEN)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"email": "%s", "username": "%s", "password": "Str0ng!Password"}""".formatted(email, username));
	}

}

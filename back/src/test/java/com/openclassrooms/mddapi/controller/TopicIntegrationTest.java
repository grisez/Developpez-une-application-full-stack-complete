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

import com.openclassrooms.mddapi.TestcontainersConfiguration;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.security.AuthCookieManager;

import jakarta.servlet.http.Cookie;

/**
 * Subscription through the whole application, down to a real MySQL database.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class TopicIntegrationTest {

	private static final Cookie CSRF_COOKIE = new Cookie("XSRF-TOKEN", "a-csrf-token-value");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TopicRepository topicRepository;

	@Autowired
	private UserRepository userRepository;

	@Test
	void subscribingTwiceKeepsASingleSubscriptionThatSurvivesAReload() throws Exception {
		// Arrange
		Cookie authCookie = mockMvc.perform(post("/api/auth/register")
						.cookie(CSRF_COOKIE)
						.header("X-XSRF-TOKEN", CSRF_COOKIE.getValue())
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"email": "henry@mdd.dev", "username": "henry", "password": "Str0ng!Password"}"""))
				.andReturn().getResponse().getCookie(AuthCookieManager.COOKIE_NAME);

		// Act
		for (int attempt = 0; attempt < 2; attempt++) {
			mockMvc.perform(post("/api/topics/2/subscribe")
							.cookie(CSRF_COOKIE, authCookie)
							.header("X-XSRF-TOKEN", CSRF_COOKIE.getValue()))
					.andExpect(status().isNoContent());
		}

		// Assert
		mockMvc.perform(get("/api/topics").cookie(authCookie))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[?(@.id == 2)].subscribed").value(true))
				.andExpect(jsonPath("$[?(@.id == 1)].subscribed").value(false));
		Long userId = userRepository.findByUsername("henry").orElseThrow().getId();
		assertThat(topicRepository.findSubscribedTopicIds(userId)).containsExactly(2);
	}

	@Test
	void subscribingWithoutTheCsrfTokenIsForbidden() throws Exception {
		// Arrange
		Cookie authCookie = mockMvc.perform(post("/api/auth/register")
						.cookie(CSRF_COOKIE)
						.header("X-XSRF-TOKEN", CSRF_COOKIE.getValue())
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"email": "iris@mdd.dev", "username": "iris", "password": "Str0ng!Password"}"""))
				.andReturn().getResponse().getCookie(AuthCookieManager.COOKIE_NAME);

		// Act and assert: a malicious site can make the browser send the cookie, but not the header
		mockMvc.perform(post("/api/topics/3/subscribe").cookie(authCookie))
				.andExpect(status().isForbidden());
	}

}

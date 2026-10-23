package com.openclassrooms.mddapi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.jayway.jsonpath.JsonPath;
import com.openclassrooms.mddapi.TestcontainersConfiguration;
import com.openclassrooms.mddapi.security.AuthCookieManager;

import jakarta.servlet.http.Cookie;

/**
 * Publication of an article through the whole application, down to a real MySQL database.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class PostIntegrationTest {

	private static final Cookie CSRF_COOKIE = new Cookie("XSRF-TOKEN", "a-csrf-token-value");

	@Autowired
	private MockMvc mockMvc;

	@Test
	void publishedArticleGetsItsAuthorAndDateFromTheServerAndReachesTheSubscribersFeed() throws Exception {
		// Arrange
		Cookie author = register("paul");
		Cookie subscriber = register("quinn");
		mockMvc.perform(withCsrf(post("/api/topics/5/subscribe")).cookie(CSRF_COOKIE, subscriber));
		Instant beforePublication = Instant.now();

		// Act: the author and the date sent by the client must be ignored
		String body = mockMvc.perform(withCsrf(post("/api/articles")).cookie(CSRF_COOKIE, author).content("""
						{"topicId": 5, "title": "Hello", "content": "First post",
						 "authorUsername": "someone-else", "createdAt": "2000-01-01T00:00:00Z"}"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.authorUsername").value("paul"))
				.andReturn().getResponse().getContentAsString();

		// Assert
		Instant createdAt = Instant.parse(JsonPath.read(body, "$.createdAt"));
		assertThat(createdAt).isAfterOrEqualTo(beforePublication.minusSeconds(1));
		mockMvc.perform(get("/api/articles").cookie(subscriber))
				.andExpect(jsonPath("$[0].title").value("Hello"))
				.andExpect(jsonPath("$[0].authorUsername").value("paul"));
	}

	private Cookie register(String username) throws Exception {
		return mockMvc.perform(withCsrf(post("/api/auth/register")).content("""
						{"email": "%s@mdd.dev", "username": "%s", "password": "Str0ng!Password"}"""
						.formatted(username, username)))
				.andReturn().getResponse().getCookie(AuthCookieManager.COOKIE_NAME);
	}

	private static MockHttpServletRequestBuilder withCsrf(MockHttpServletRequestBuilder request) {
		return request.cookie(CSRF_COOKIE)
				.header("X-XSRF-TOKEN", CSRF_COOKIE.getValue())
				.contentType(MediaType.APPLICATION_JSON);
	}

}

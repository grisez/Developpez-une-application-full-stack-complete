package com.openclassrooms.mddapi;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.security.AuthCookieManager;

import jakarta.servlet.http.Cookie;

/**
 * The demo data of the dev profile: loaded at startup, and the demo accounts can log in with the documented password.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Import(TestcontainersConfiguration.class)
class DevSeedDataIntegrationTest {

	private static final Cookie CSRF_COOKIE = new Cookie("XSRF-TOKEN", "a-csrf-token-value");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PostRepository postRepository;

	@Test
	void demoAccountLogsInAndSeesTheArticlesOfItsTopics() throws Exception {
		// Act
		Cookie authCookie = mockMvc.perform(post("/api/auth/login")
						.cookie(CSRF_COOKIE)
						.header("X-XSRF-TOKEN", CSRF_COOKIE.getValue())
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"identifier": "alice", "password": "Demo!Mdd2026"}"""))
				.andExpect(status().isOk())
				.andReturn().getResponse().getCookie(AuthCookieManager.COOKIE_NAME);

		// Assert
		assertThat(postRepository.count()).isGreaterThanOrEqualTo(8);
		mockMvc.perform(get("/api/articles").cookie(authCookie))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].title").value("Valider ses formulaires Angular"));
	}

}

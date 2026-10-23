package com.openclassrooms.mddapi.security;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.mddapi.controller.TopicController;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.service.TopicService;

import jakarta.servlet.http.Cookie;

/**
 * Checks the security rules end to end through the filter chain, with real signed tokens sent in the cookie.
 */
@WebMvcTest(TopicController.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JwtEncoder jwtEncoder;

	@Autowired
	private JwtProperties jwtProperties;

	@MockitoBean
	private TopicService topicService;

	@Test
	void requestWithoutTokenIsRejectedWithUnauthorizedProblem() throws Exception {
		// Act and assert
		mockMvc.perform(get("/api/topics"))
				.andExpect(status().isUnauthorized())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
				.andExpect(jsonPath("$.status").value(401));
	}

	@Test
	void requestWithValidTokenCookieIsAuthenticated() throws Exception {
		// Arrange
		when(topicService.getTopics(1L)).thenReturn(List.of());
		String token = new JwtService(jwtEncoder, jwtProperties).generateToken(user(1L));

		// Act and assert
		mockMvc.perform(get("/api/topics").cookie(authCookie(token)))
				.andExpect(status().isOk());
	}

	@Test
	void requestWithForgedTokenIsRejected() throws Exception {
		// Act and assert
		mockMvc.perform(get("/api/topics").cookie(authCookie("not.a.valid-token")))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void requestWithExpiredTokenIsRejected() throws Exception {
		// Arrange
		Instant past = Instant.now().minusSeconds(3600);
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer(JwtService.ISSUER)
				.subject("1")
				.issuedAt(past.minusSeconds(60))
				.expiresAt(past)
				.build();
		String expiredToken = jwtEncoder
				.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
				.getTokenValue();

		// Act and assert
		mockMvc.perform(get("/api/topics").cookie(authCookie(expiredToken)))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void stateChangingRequestWithoutCsrfTokenIsForbidden() throws Exception {
		// Arrange
		String token = new JwtService(jwtEncoder, jwtProperties).generateToken(user(1L));

		// Act and assert
		mockMvc.perform(post("/api/topics").cookie(authCookie(token)))
				.andExpect(status().isForbidden())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
	}

	@Test
	void publicEndpointIsReachableWithoutTokenWhenTheCsrfHeaderMatchesTheCookie() throws Exception {
		// Arrange: what Angular sends, the XSRF-TOKEN cookie value copied into the X-XSRF-TOKEN header
		String csrfToken = "a-csrf-token-value";

		// Act and assert: the auth controller is not loaded in this test, so passing security means reaching a 404
		mockMvc.perform(post("/api/auth/login")
						.cookie(new Cookie("XSRF-TOKEN", csrfToken))
						.header("X-XSRF-TOKEN", csrfToken))
				.andExpect(status().isNotFound());
	}

	@Test
	void publicEndpointsIgnoreAnInvalidTokenCookieSoThatUsersAreNeverStuck() throws Exception {
		// Arrange: for example a token signed with a key that has since been replaced
		String csrfToken = "a-csrf-token-value";

		// Act and assert
		for (String path : new String[] { "/api/auth/login", "/api/auth/logout" }) {
			mockMvc.perform(post(path)
							.cookie(new Cookie("XSRF-TOKEN", csrfToken), authCookie("not.a.valid-token"))
							.header("X-XSRF-TOKEN", csrfToken))
					.andExpect(status().isNotFound());
		}
	}

	@Test
	void responseProvidesTheCsrfCookieReadableByTheFrontEnd() throws Exception {
		// Act and assert
		mockMvc.perform(get("/api/topics"))
				.andExpect(cookie().exists("XSRF-TOKEN"))
				.andExpect(cookie().httpOnly("XSRF-TOKEN", false));
	}

	private static Cookie authCookie(String token) {
		return new Cookie(AuthCookieManager.COOKIE_NAME, token);
	}

	private static User user(Long id) {
		User user = new User();
		user.setId(id);
		return user;
	}

}

package com.openclassrooms.mddapi.config;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.mddapi.TestcontainersConfiguration;

/**
 * The OpenAPI description is published without authentication and describes every endpoint and the cookie security.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class OpenApiIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void apiDescriptionListsTheEndpointsAndTheCookieAuthentication() throws Exception {
		// Act and assert
		mockMvc.perform(get("/v3/api-docs"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.paths", hasKey("/api/auth/login")))
				.andExpect(jsonPath("$.paths", hasKey("/api/articles/{postId}/comments")))
				.andExpect(jsonPath("$.components.securitySchemes.cookieAuth.in").value("cookie"))
				.andExpect(jsonPath("$.components.securitySchemes.cookieAuth.name").value("mdd_token"))
				.andExpect(jsonPath("$.paths['/api/auth/login'].post.security").isEmpty())
				.andExpect(jsonPath("$.paths['/api/me'].get.responses", hasKey("401")))
				.andExpect(jsonPath("$.paths['/api/me'].get.responses", not(hasKey("500"))));
	}

	@Test
	void swaggerUiIsReachableWithoutAuthentication() throws Exception {
		// Act and assert
		mockMvc.perform(get("/swagger-ui/index.html"))
				.andExpect(status().isOk());
	}

}

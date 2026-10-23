package com.openclassrooms.mddapi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import com.openclassrooms.mddapi.security.AuthCookieManager;

import jakarta.servlet.http.Cookie;

/**
 * Profile update through the whole application, down to a real MySQL database.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class ProfileIntegrationTest {

	private static final Cookie CSRF_COOKIE = new Cookie("XSRF-TOKEN", "a-csrf-token-value");

	@Autowired
	private MockMvc mockMvc;

	@Test
	void updatedProfileKeepsTheSessionAndOnlyTheNewPasswordLogsIn() throws Exception {
		// Arrange
		Cookie authCookie = mockMvc.perform(withCsrf(post("/api/auth/register"))
						.content("""
								{"email": "jack@mdd.dev", "username": "jack", "password": "Str0ng!Password"}"""))
				.andReturn().getResponse().getCookie(AuthCookieManager.COOKIE_NAME);

		// Act
		mockMvc.perform(withCsrf(put("/api/me")).cookie(CSRF_COOKIE, authCookie)
						.content("""
								{"email": "jacky@mdd.dev", "username": "jacky", "password": "N3w!Password"}"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("jacky"));

		// Assert
		mockMvc.perform(get("/api/me").cookie(authCookie))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("jacky@mdd.dev"));
		mockMvc.perform(withCsrf(post("/api/auth/login")).content("""
						{"identifier": "jacky", "password": "N3w!Password"}"""))
				.andExpect(status().isOk());
		mockMvc.perform(withCsrf(post("/api/auth/login")).content("""
						{"identifier": "jacky", "password": "Str0ng!Password"}"""))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void updatingWithTheEmailOfAnotherAccountIsRejected() throws Exception {
		// Arrange
		mockMvc.perform(withCsrf(post("/api/auth/register")).content("""
				{"email": "kate@mdd.dev", "username": "kate", "password": "Str0ng!Password"}"""));
		Cookie authCookie = mockMvc.perform(withCsrf(post("/api/auth/register"))
						.content("""
								{"email": "liam@mdd.dev", "username": "liam", "password": "Str0ng!Password"}"""))
				.andReturn().getResponse().getCookie(AuthCookieManager.COOKIE_NAME);

		// Act and assert
		mockMvc.perform(withCsrf(put("/api/me")).cookie(CSRF_COOKIE, authCookie)
						.content("""
								{"email": "KATE@mdd.dev", "username": "liam"}"""))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.errors.email").exists());
	}

	private static MockHttpServletRequestBuilder withCsrf(MockHttpServletRequestBuilder request) {
		return request.cookie(CSRF_COOKIE)
				.header("X-XSRF-TOKEN", CSRF_COOKIE.getValue())
				.contentType(MediaType.APPLICATION_JSON);
	}

}

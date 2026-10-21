package com.openclassrooms.mddapi.controller;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.openclassrooms.mddapi.dto.RegisterRequest;
import com.openclassrooms.mddapi.dto.UserResponse;
import com.openclassrooms.mddapi.exception.EmailAlreadyUsedException;
import com.openclassrooms.mddapi.security.AuthCookieManager;
import com.openclassrooms.mddapi.security.SecurityConfig;
import com.openclassrooms.mddapi.service.AuthResult;
import com.openclassrooms.mddapi.service.AuthService;
import com.openclassrooms.mddapi.service.UserService;

import jakarta.servlet.http.Cookie;

@WebMvcTest(AuthController.class)
@Import({ SecurityConfig.class, AuthCookieManager.class })
class AuthControllerTest {

	private static final String CSRF_TOKEN = "a-csrf-token-value";

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AuthService authService;

	@MockitoBean
	private UserService userService;

	@Test
	void registerCreatesTheAccountAndSetsTheAuthenticationCookie() throws Exception {
		// Arrange
		when(authService.register(any(RegisterRequest.class)))
				.thenReturn(new AuthResult(new UserResponse(1L, "alice", "alice@mdd.dev"), "signed-token"));

		// Act and assert
		mockMvc.perform(register("alice@mdd.dev", "alice", "Str0ng!Password"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.username").value("alice"))
				.andExpect(jsonPath("$.token").doesNotExist())
				.andExpect(header().string(HttpHeaders.SET_COOKIE, allOf(
						containsString(AuthCookieManager.COOKIE_NAME + "=signed-token"),
						containsString("HttpOnly"),
						containsString("SameSite=Strict"),
						containsString("Max-Age=604800"))));
	}

	@Test
	void registerRejectsAWeakPasswordWithTheFieldError() throws Exception {
		// Act and assert
		mockMvc.perform(register("alice@mdd.dev", "alice", "password"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors.password").exists())
				.andExpect(jsonPath("$.errors.email").doesNotExist());
	}

	@Test
	void registerRejectsAnInvalidEmailAndATooShortUsername() throws Exception {
		// Act and assert
		mockMvc.perform(register("not-an-email", "al", "Str0ng!Password"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors.email").exists())
				.andExpect(jsonPath("$.errors.username").exists());
	}

	@Test
	void registerReturnsConflictWhenTheEmailIsAlreadyUsed() throws Exception {
		// Arrange
		when(authService.register(any(RegisterRequest.class))).thenThrow(new EmailAlreadyUsedException());

		// Act and assert
		mockMvc.perform(register("alice@mdd.dev", "alice", "Str0ng!Password"))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.detail").value("This email address is already used."))
				.andExpect(jsonPath("$.errors.email").exists());
	}

	@Test
	void registerWithoutCsrfTokenIsForbidden() throws Exception {
		// Act and assert
		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json("alice@mdd.dev", "alice", "Str0ng!Password")))
				.andExpect(status().isForbidden());
	}

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

	/** A registration request carrying the CSRF cookie and header, as Angular sends it. */
	private static MockHttpServletRequestBuilder register(String email, String username, String password) {
		return post("/api/auth/register")
				.cookie(new Cookie("XSRF-TOKEN", CSRF_TOKEN))
				.header("X-XSRF-TOKEN", CSRF_TOKEN)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json(email, username, password));
	}

	private static String json(String email, String username, String password) {
		return """
				{"email": "%s", "username": "%s", "password": "%s"}""".formatted(email, username, password);
	}

}

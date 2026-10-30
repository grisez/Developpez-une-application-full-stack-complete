package com.openclassrooms.mddapi.security;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Writes security errors in the same problem details format as the rest of the API.
 * They are raised by the security filters, before any controller, so the global exception handler never sees them.
 */
@RequiredArgsConstructor
public class ProblemDetailSecurityHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

	private final ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException {
		write(request, response, HttpStatus.UNAUTHORIZED, "Authentication is required to access this resource.");
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException exception) throws IOException {
		write(request, response, HttpStatus.FORBIDDEN, "Access to this resource is denied.");
	}

	private void write(HttpServletRequest request, HttpServletResponse response, HttpStatus status, String detail)
			throws IOException {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
		problem.setInstance(URI.create(request.getRequestURI()));
		response.setStatus(status.value());
		response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
		objectMapper.writeValue(response.getOutputStream(), problem);
	}

}

package com.openclassrooms.mddapi.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * Turns every error into a uniform JSON response (RFC 9457 problem details) that never exposes internal details.
 * Spring MVC errors (invalid input, unknown route, wrong method) are handled by the parent class.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	/**
	 * Adds the message of every invalid field, so that the front-end can display it next to the field.
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		Map<String, String> errors = new LinkedHashMap<>();
		for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
			errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
		}
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Some fields are invalid.");
		problem.setProperty("errors", errors);
		return ResponseEntity.badRequest().body(problem);
	}

	/**
	 * Handles a request about a user that does not exist.
	 *
	 * @param exception the missing user error
	 * @return a 404 problem detail
	 */
	@ExceptionHandler(UserNotFoundException.class)
	public ProblemDetail handleUserNotFound(UserNotFoundException exception) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "User not found.");
	}

	/**
	 * Handles a request about a topic that does not exist.
	 *
	 * @param exception the missing topic error
	 * @return a 404 problem detail
	 */
	@ExceptionHandler(TopicNotFoundException.class)
	public ProblemDetail handleTopicNotFound(TopicNotFoundException exception) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Topic not found.");
	}

	/**
	 * Handles a request about an article that does not exist.
	 *
	 * @param exception the missing article error
	 * @return a 404 problem detail
	 */
	@ExceptionHandler(PostNotFoundException.class)
	public ProblemDetail handlePostNotFound(PostNotFoundException exception) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Article not found.");
	}

	/**
	 * Handles a failed login with a generic message that does not tell which of the two fields is wrong.
	 *
	 * @param exception the failed login
	 * @return a 401 problem detail
	 */
	@ExceptionHandler(InvalidCredentialsException.class)
	public ProblemDetail handleInvalidCredentials(InvalidCredentialsException exception) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
	}

	/**
	 * Handles an email that another account already uses.
	 *
	 * @param exception the duplicate error
	 * @return a 409 problem detail naming the field, in the same {@code errors} format as validation errors
	 */
	@ExceptionHandler(EmailAlreadyUsedException.class)
	public ProblemDetail handleEmailAlreadyUsed(EmailAlreadyUsedException exception) {
		return conflictOnField("email", exception.getMessage());
	}

	/**
	 * Handles a username that another account already uses.
	 *
	 * @param exception the duplicate error
	 * @return a 409 problem detail naming the field, in the same {@code errors} format as validation errors
	 */
	@ExceptionHandler(UsernameAlreadyUsedException.class)
	public ProblemDetail handleUsernameAlreadyUsed(UsernameAlreadyUsedException exception) {
		return conflictOnField("username", exception.getMessage());
	}

	/**
	 * Handles a unique constraint violated in the database, for example by two identical registrations sent
	 * at the same time and both accepted by the checks done beforehand.
	 *
	 * @param exception the constraint violation
	 * @return a 409 problem detail
	 */
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException exception) {
		// The database message is not logged: it contains the duplicated value, such as an email address
		log.warn("Data integrity violation rejected with 409");
		return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "The request conflicts with existing data.");
	}

	/**
	 * Handles any unexpected error: the cause is logged on the server, the client only gets a generic message.
	 *
	 * @param exception the unexpected error
	 * @return a 500 problem detail
	 */
	@ExceptionHandler(Exception.class)
	public ProblemDetail handleUnexpected(Exception exception) {
		log.error("Unexpected error", exception);
		return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
	}

	private static ProblemDetail conflictOnField(String field, String message) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, message);
		problem.setProperty("errors", Map.of(field, message));
		return problem;
	}

}

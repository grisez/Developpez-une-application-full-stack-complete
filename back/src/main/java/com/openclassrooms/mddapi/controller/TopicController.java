package com.openclassrooms.mddapi.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.mddapi.dto.TopicResponse;
import com.openclassrooms.mddapi.service.TopicService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * REST endpoints for topics.
 */
@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
@Tag(name = "Topics", description = "Topics and subscriptions")
public class TopicController {

	private final TopicService topicService;

	/**
	 * Lists every topic, with the subscription state of the logged-in user.
	 *
	 * @param jwt the validated token of the current request
	 * @return all topics
	 */
	@GetMapping
	@Operation(summary = "List every topic", description = "Each topic tells whether the logged-in user follows it.")
	@ApiResponse(responseCode = "200", description = "All topics")
	@ApiResponse(responseCode = "401", description = "No valid session")
	public List<TopicResponse> getTopics(@AuthenticationPrincipal Jwt jwt) {
		return topicService.getTopics(Long.valueOf(jwt.getSubject()));
	}

	/**
	 * Subscribes the logged-in user to a topic. Calling it again for the same topic changes nothing.
	 *
	 * @param jwt the validated token of the current request
	 * @param id  the topic to follow
	 * @return 204 once subscribed
	 */
	@PostMapping("/{id}/subscribe")
	@Operation(summary = "Subscribe to a topic", description = "Idempotent: subscribing twice changes nothing.")
	@ApiResponse(responseCode = "204", description = "Subscribed")
	@ApiResponse(responseCode = "401", description = "No valid session")
	@ApiResponse(responseCode = "403", description = "Missing or invalid CSRF token")
	@ApiResponse(responseCode = "404", description = "Unknown topic")
	public ResponseEntity<Void> subscribe(@AuthenticationPrincipal Jwt jwt, @PathVariable Integer id) {
		topicService.subscribe(Long.valueOf(jwt.getSubject()), id);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Unsubscribes the logged-in user from a topic. Calling it for a topic the user does not follow changes nothing.
	 *
	 * @param jwt the validated token of the current request
	 * @param id  the topic to stop following
	 * @return 204 once unsubscribed
	 */
	@DeleteMapping("/{id}/subscribe")
	@Operation(summary = "Unsubscribe from a topic", description = "Idempotent: unsubscribing twice changes nothing.")
	@ApiResponse(responseCode = "204", description = "Unsubscribed")
	@ApiResponse(responseCode = "401", description = "No valid session")
	@ApiResponse(responseCode = "403", description = "Missing or invalid CSRF token")
	@ApiResponse(responseCode = "404", description = "Unknown topic")
	public ResponseEntity<Void> unsubscribe(@AuthenticationPrincipal Jwt jwt, @PathVariable Integer id) {
		topicService.unsubscribe(Long.valueOf(jwt.getSubject()), id);
		return ResponseEntity.noContent().build();
	}

}

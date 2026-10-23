package com.openclassrooms.mddapi.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.mddapi.dto.TopicResponse;
import com.openclassrooms.mddapi.service.TopicService;

import lombok.RequiredArgsConstructor;

/**
 * REST endpoints for topics.
 */
@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

	private final TopicService topicService;

	/**
	 * Lists every topic, with the subscription state of the logged-in user.
	 *
	 * @param jwt the validated token of the current request
	 * @return all topics
	 */
	@GetMapping
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
	public ResponseEntity<Void> subscribe(@AuthenticationPrincipal Jwt jwt, @PathVariable Integer id) {
		topicService.subscribe(Long.valueOf(jwt.getSubject()), id);
		return ResponseEntity.noContent().build();
	}

}

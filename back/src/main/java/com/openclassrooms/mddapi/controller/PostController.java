package com.openclassrooms.mddapi.controller;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.mddapi.dto.CreatePostRequest;
import com.openclassrooms.mddapi.dto.PostResponse;
import com.openclassrooms.mddapi.service.PostService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

/**
 * REST endpoints for articles, called posts in the code.
 */
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	/**
	 * Returns the news feed of the logged-in user.
	 *
	 * @param jwt  the validated token of the current request
	 * @param sort {@code desc} (default) for the most recent first, {@code asc} for the oldest first
	 * @return the posts of the topics the user is subscribed to
	 */
	@GetMapping
	public List<PostResponse> getFeed(@AuthenticationPrincipal Jwt jwt,
			@RequestParam(defaultValue = "desc") @Pattern(regexp = "asc|desc") String sort) {
		return postService.getFeed(Long.valueOf(jwt.getSubject()), Sort.Direction.fromString(sort));
	}

	/**
	 * Publishes an article written by the logged-in user.
	 *
	 * @param jwt     the validated token of the current request
	 * @param request the topic, the title and the content
	 * @return 201 with the created article and its location
	 */
	@PostMapping
	public ResponseEntity<PostResponse> create(@AuthenticationPrincipal Jwt jwt,
			@Valid @RequestBody CreatePostRequest request) {
		PostResponse post = postService.create(Long.valueOf(jwt.getSubject()), request);
		return ResponseEntity.created(URI.create("/api/articles/" + post.id())).body(post);
	}

}

package com.openclassrooms.mddapi.controller;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.mddapi.dto.CreatePostRequest;
import com.openclassrooms.mddapi.dto.PostDetailResponse;
import com.openclassrooms.mddapi.dto.PostResponse;
import com.openclassrooms.mddapi.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

/**
 * REST endpoints for articles, called posts in the code.
 */
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
@Tag(name = "Articles", description = "News feed, articles and their publication")
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
	@Operation(summary = "Get the news feed", description = "Articles of the topics the logged-in user follows.")
	@ApiResponse(responseCode = "200", description = "The articles of the feed")
	@ApiResponse(responseCode = "400", description = "Unknown sort order")
	@ApiResponse(responseCode = "401", description = "No valid session")
	public List<PostResponse> getFeed(@AuthenticationPrincipal Jwt jwt,
			@Parameter(description = "desc for the most recent first, asc for the oldest first")
			@RequestParam(defaultValue = "desc") @Pattern(regexp = "asc|desc") String sort) {
		return postService.getFeed(Long.valueOf(jwt.getSubject()), Sort.Direction.fromString(sort));
	}

	/**
	 * Returns an article with its comments, oldest first.
	 *
	 * @param id the article id
	 * @return the article and its comments
	 */
	@GetMapping("/{id}")
	@Operation(summary = "Get an article", description = "The article with its comments, oldest first.")
	@ApiResponse(responseCode = "200", description = "The article and its comments")
	@ApiResponse(responseCode = "401", description = "No valid session")
	@ApiResponse(responseCode = "404", description = "Unknown article")
	public PostDetailResponse getById(@PathVariable Long id) {
		return postService.getById(id);
	}

	/**
	 * Publishes an article written by the logged-in user.
	 *
	 * @param jwt     the validated token of the current request
	 * @param request the topic, the title and the content
	 * @return 201 with the created article and its location
	 */
	@PostMapping
	@Operation(summary = "Publish an article", description = "The author and the date are set by the server.")
	@ApiResponse(responseCode = "201", description = "Article published, with its location")
	@ApiResponse(responseCode = "400", description = "Missing topic, title or content, or too long")
	@ApiResponse(responseCode = "401", description = "No valid session")
	@ApiResponse(responseCode = "403", description = "Missing or invalid CSRF token")
	@ApiResponse(responseCode = "404", description = "Unknown topic")
	public ResponseEntity<PostResponse> create(@AuthenticationPrincipal Jwt jwt,
			@Valid @RequestBody CreatePostRequest request) {
		PostResponse post = postService.create(Long.valueOf(jwt.getSubject()), request);
		return ResponseEntity.created(URI.create("/api/articles/" + post.id())).body(post);
	}

}

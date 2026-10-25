package com.openclassrooms.mddapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.mddapi.dto.CommentResponse;
import com.openclassrooms.mddapi.dto.CreateCommentRequest;
import com.openclassrooms.mddapi.service.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST endpoints for the comments of an article.
 */
@RestController
@RequestMapping("/api/articles/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	/**
	 * Adds a comment written by the logged-in user to an article.
	 *
	 * @param jwt     the validated token of the current request
	 * @param postId  the commented article
	 * @param request the text of the comment
	 * @return 201 with the created comment
	 */
	@PostMapping
	public ResponseEntity<CommentResponse> create(@AuthenticationPrincipal Jwt jwt, @PathVariable Long postId,
			@Valid @RequestBody CreateCommentRequest request) {
		CommentResponse comment = commentService.create(postId, Long.valueOf(jwt.getSubject()), request);
		// No Location header: a comment is only read with its article, it has no page of its own
		return ResponseEntity.status(HttpStatus.CREATED).body(comment);
	}

}

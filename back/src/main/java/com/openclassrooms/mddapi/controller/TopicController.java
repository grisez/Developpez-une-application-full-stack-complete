package com.openclassrooms.mddapi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
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
	 * Lists every topic.
	 *
	 * @return all topics
	 */
	@GetMapping
	public List<TopicResponse> getTopics() {
		return topicService.getTopics();
	}

}

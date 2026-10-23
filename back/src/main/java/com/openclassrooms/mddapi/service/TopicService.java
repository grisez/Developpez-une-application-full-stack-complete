package com.openclassrooms.mddapi.service;

import java.util.List;

import com.openclassrooms.mddapi.dto.TopicResponse;

/**
 * Business operations on topics.
 */
public interface TopicService {

	/**
	 * Returns every topic, whether the user is subscribed to it or not, with their subscription state.
	 *
	 * @param userId the logged-in user
	 * @return all topics
	 */
	List<TopicResponse> getTopics(Long userId);

}

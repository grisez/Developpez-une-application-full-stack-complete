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

	/**
	 * Subscribes a user to a topic. Subscribing again to the same topic changes nothing.
	 *
	 * @param userId  the logged-in user
	 * @param topicId the topic to follow
	 * @throws com.openclassrooms.mddapi.exception.TopicNotFoundException if the topic does not exist
	 */
	void subscribe(Long userId, Integer topicId);

}

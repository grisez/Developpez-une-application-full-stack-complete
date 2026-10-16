package com.openclassrooms.mddapi.service;

import java.util.List;

import com.openclassrooms.mddapi.dto.TopicResponse;

/**
 * Business operations on topics.
 */
public interface TopicService {

	/**
	 * Returns every topic, whether the user is subscribed to it or not.
	 *
	 * @return all topics
	 */
	List<TopicResponse> getTopics();

}

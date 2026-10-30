package com.openclassrooms.mddapi.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.openclassrooms.mddapi.model.Topic;

/**
 * Data access to topics and to the subscriptions of the users.
 */
@Repository
public interface TopicRepository extends JpaRepository<Topic, Integer> {

	/**
	 * Returns the ids of the topics a user is subscribed to, in a single query.
	 *
	 * @param userId the user id
	 * @return the subscribed topic ids
	 */
	@Query("select topic.id from User user join user.subscriptions topic where user.id = :userId")
	Set<Integer> findSubscribedTopicIds(Long userId);

	/**
	 * Returns the topics a user is subscribed to, sorted by name, in a single query.
	 *
	 * @param userId the user id
	 * @return the subscribed topics
	 */
	@Query("select topic from User user join user.subscriptions topic where user.id = :userId order by topic.name")
	List<Topic> findSubscribedTopics(Long userId);

}

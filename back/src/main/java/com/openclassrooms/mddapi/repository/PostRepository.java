package com.openclassrooms.mddapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.openclassrooms.mddapi.model.Post;

/**
 * Data access to articles.
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	/**
	 * Returns the posts of the topics a user is subscribed to. The author and the topic are fetched in the same
	 * query, so that displaying the feed never triggers one extra query per post.
	 *
	 * @param userId the user whose feed is built
	 * @param sort   the order of the posts
	 * @return the posts of the feed
	 */
	@Query("""
			select post from Post post
			join fetch post.author
			join fetch post.topic
			where post.topic.id in (
				select topic.id from User user join user.subscriptions topic where user.id = :userId)""")
	List<Post> findFeed(Long userId, Sort sort);

	/**
	 * Returns an article with its author and its topic fetched in the same query.
	 *
	 * @param id the article id
	 * @return the article, if it exists
	 */
	@Query("select post from Post post join fetch post.author join fetch post.topic where post.id = :id")
	Optional<Post> findDetailById(Long id);

}

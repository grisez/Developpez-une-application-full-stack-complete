package com.openclassrooms.mddapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.openclassrooms.mddapi.model.Comment;

/**
 * Data access to the comments of the articles.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

	/**
	 * Returns the comments of an article, oldest first, with their author fetched in the same query.
	 *
	 * @param postId the article id
	 * @return the comments of the article
	 */
	@Query("""
			select comment from Comment comment
			join fetch comment.author
			where comment.post.id = :postId
			order by comment.createdAt, comment.id""")
	List<Comment> findByPostIdOldestFirst(Long postId);

}

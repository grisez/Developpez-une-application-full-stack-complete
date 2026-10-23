package com.openclassrooms.mddapi.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import com.openclassrooms.mddapi.TestcontainersConfiguration;
import com.openclassrooms.mddapi.model.Comment;
import com.openclassrooms.mddapi.model.Post;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class PersistenceIntegrationTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TopicRepository topicRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Test
	void referenceTopicsAreInsertedByFlyway() {
		// Act
		long topicCount = topicRepository.count();

		// Assert
		assertThat(topicCount).isEqualTo(8);
	}

	@Test
	void savedPostGetsCreationDateAndKeepsItsAuthorAndTopic() {
		// Arrange
		User author = userRepository.save(newUser("alice@mdd.dev", "alice"));
		Topic topic = topicRepository.findAll().getFirst();
		Post post = new Post();
		post.setTitle("Hello MDD");
		post.setContent("First post");
		post.setAuthor(author);
		post.setTopic(topic);

		// Act
		Long postId = postRepository.saveAndFlush(post).getId();
		entityManager.clear();
		Post reloaded = postRepository.findById(postId).orElseThrow();

		// Assert
		assertThat(reloaded.getCreatedAt()).isNotNull();
		assertThat(reloaded.getAuthor().getId()).isEqualTo(author.getId());
		assertThat(reloaded.getTopic().getId()).isEqualTo(topic.getId());
	}

	@Test
	void savedCommentIsLinkedToItsPost() {
		// Arrange
		User author = userRepository.save(newUser("bob@mdd.dev", "bob"));
		Post post = new Post();
		post.setTitle("A post");
		post.setContent("Content");
		post.setAuthor(author);
		post.setTopic(topicRepository.findAll().getFirst());
		postRepository.save(post);
		Comment comment = new Comment();
		comment.setContent("Nice post");
		comment.setAuthor(author);
		comment.setPost(post);

		// Act
		Long commentId = commentRepository.saveAndFlush(comment).getId();
		entityManager.clear();
		Comment reloaded = commentRepository.findById(commentId).orElseThrow();

		// Assert
		assertThat(reloaded.getCreatedAt()).isNotNull();
		assertThat(reloaded.getPost().getId()).isEqualTo(post.getId());
	}

	@Test
	void subscriptionsAreStoredInTheJoinTable() {
		// Arrange
		User user = newUser("carol@mdd.dev", "carol");
		user.getSubscriptions().add(topicRepository.findAll().getFirst());

		// Act
		Long userId = userRepository.saveAndFlush(user).getId();
		entityManager.clear();
		User reloaded = userRepository.findById(userId).orElseThrow();

		// Assert
		assertThat(reloaded.getSubscriptions()).hasSize(1);
	}

	@Test
	void subscribedTopicIdsAreThoseOfTheUserOnly() {
		// Arrange
		Topic first = topicRepository.findAll().get(0);
		Topic second = topicRepository.findAll().get(1);
		User subscriber = newUser("erin@mdd.dev", "erin");
		subscriber.getSubscriptions().add(first);
		User other = newUser("frank@mdd.dev", "frank");
		other.getSubscriptions().add(second);
		Long subscriberId = userRepository.save(subscriber).getId();
		userRepository.saveAndFlush(other);

		// Act
		var subscribedTopicIds = topicRepository.findSubscribedTopicIds(subscriberId);

		// Assert
		assertThat(subscribedTopicIds).containsExactly(first.getId());
	}

	@Test
	void subscribedTopicsAreSortedByName() {
		// Arrange
		User user = newUser("grace@mdd.dev", "grace");
		user.getSubscriptions().addAll(topicRepository.findAll());
		Long userId = userRepository.saveAndFlush(user).getId();

		// Act
		var names = topicRepository.findSubscribedTopics(userId).stream().map(Topic::getName).toList();

		// Assert
		assertThat(names).hasSize(8).isSortedAccordingTo(String.CASE_INSENSITIVE_ORDER);
	}

	@Test
	void emailUniquenessIgnoresCase() {
		// Arrange
		userRepository.saveAndFlush(newUser("Dave@mdd.dev", "dave"));
		User duplicate = newUser("dave@mdd.dev", "dave2");

		// Act and assert
		assertThatThrownBy(() -> userRepository.saveAndFlush(duplicate))
				.isInstanceOf(DataIntegrityViolationException.class);
	}

	private static User newUser(String email, String username) {
		User user = new User();
		user.setEmail(email);
		user.setUsername(username);
		user.setPassword("not-a-real-hash");
		return user;
	}

}

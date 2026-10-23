package com.openclassrooms.mddapi.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.openclassrooms.mddapi.dto.TopicResponse;
import com.openclassrooms.mddapi.mapper.TopicMapper;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.repository.TopicRepository;

@ExtendWith(MockitoExtension.class)
class TopicServiceImplTest {

	@Mock
	private TopicRepository topicRepository;

	private TopicServiceImpl topicService;

	@BeforeEach
	void setUp() {
		// The real generated mapper is used so the test also checks the field mapping
		topicService = new TopicServiceImpl(topicRepository, Mappers.getMapper(TopicMapper.class));
	}

	@Test
	void getTopicsReturnsEveryTopicWithTheSubscriptionStateOfTheUser() {
		// Arrange
		when(topicRepository.findAll(any(Sort.class))).thenReturn(List.of(topic(1, "Java"), topic(2, "Angular")));
		when(topicRepository.findSubscribedTopicIds(7L)).thenReturn(Set.of(2));

		// Act
		List<TopicResponse> topics = topicService.getTopics(7L);

		// Assert
		assertThat(topics).containsExactly(
				new TopicResponse(1, "Java", "Java description", false),
				new TopicResponse(2, "Angular", "Angular description", true));
	}

	@Test
	void getTopicsReturnsEmptyListWhenThereIsNoTopic() {
		// Arrange
		when(topicRepository.findAll(any(Sort.class))).thenReturn(List.of());
		when(topicRepository.findSubscribedTopicIds(7L)).thenReturn(Set.of());

		// Act
		List<TopicResponse> topics = topicService.getTopics(7L);

		// Assert
		assertThat(topics).isEmpty();
	}

	private static Topic topic(int id, String name) {
		Topic topic = new Topic();
		topic.setId(id);
		topic.setName(name);
		topic.setDescription(name + " description");
		return topic;
	}

}

package com.openclassrooms.mddapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A programming topic that users can subscribe to and that every post belongs to.
 */
@Entity
@Table(name = "topics")
@Getter
@Setter
@NoArgsConstructor
public class Topic {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, unique = true, length = 50)
	private String name;

	@Column(nullable = false)
	private String description;

}

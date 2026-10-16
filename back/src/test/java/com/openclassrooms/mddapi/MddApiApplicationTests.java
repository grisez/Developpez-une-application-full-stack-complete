package com.openclassrooms.mddapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * Starts the whole application against MySQL: Flyway runs the migrations, then Hibernate validates the schema.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class MddApiApplicationTests {

	@Test
	void contextLoads() {
	}

}

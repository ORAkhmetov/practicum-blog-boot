package ru.practicum.blog.practicum_blog_boot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.blog.PracticumBlogBootApplication;
import ru.practicum.config.EnablePostgresTest;

@SpringBootTest(classes = PracticumBlogBootApplication.class)
@ActiveProfiles("test")
@EnablePostgresTest
class PracticumBlogBootApplicationTests {

	@Test
	void contextLoads() {
	}

}

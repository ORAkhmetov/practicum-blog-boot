package ru.practicum.post;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.blog.PracticumBlogBootApplication;
import ru.practicum.blog.model.Post;
import ru.practicum.blog.repository.PostRepository;
import ru.practicum.config.EnablePostgresTest;

@SpringBootTest(classes = PracticumBlogBootApplication.class)
@EnablePostgresTest
@ActiveProfiles("test")
public class PostIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private PostRepository postRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        // Инициализация MockMvc с контекстом приложения
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    private void createPost(String title) {
        Post post = new Post();
        post.setTitle(title);
        postRepository.save(post);
    }

    @Test
    public void allPost_ShouldReturnPostsPage() throws Exception {
        createPost("Test post");
        mockMvc.perform(get("/post/"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/index"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attribute("posts", hasItem(
                        anyOf(
                                hasProperty("title", is("Test post"))
                        )
                )))
                .andExpect(model().attribute("currentPage", 0))
                .andExpect(model().attribute("totalPages", 1))
                .andExpect(model().attribute("size", 10));
    }
}

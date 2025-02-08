package ru.practicum.post;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.blog.PracticumBlogBootApplication;
import ru.practicum.blog.model.Post;
import ru.practicum.blog.repository.PostRepository;
import ru.practicum.config.EnablePostgresTest;

@SpringBootTest(classes = PracticumBlogBootApplication.class)
@AutoConfigureMockMvc
@EnablePostgresTest
@ActiveProfiles("test")
public class PostIntegrationTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        postRepository.deleteAll();
    }

    @Test
    public void allPost_ShouldReturnPostsPage() throws Exception {
        createPost();
        mockMvc.perform(get("/post/"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/index"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attribute("posts", hasItem(
                        anyOf(
                                hasProperty("title", is("Test post title"))
                        )
                )))
                .andExpect(model().attribute("currentPage", 0))
                .andExpect(model().attribute("totalPages", 1))
                .andExpect(model().attribute("size", 10));
    }

    private void createPost() {
        Post post = new Post();
        post.setTitle("Test post title");
        postRepository.save(post);
    }
}

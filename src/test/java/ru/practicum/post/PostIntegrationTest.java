package ru.practicum.post;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.blog.model.Post;
import ru.practicum.blog.repository.PostRepository;
import ru.practicum.blog.service.PostService;
import ru.practicum.config.TestConfig;

@ContextConfiguration(classes = { TestConfig.class })
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@Transactional
public class PostIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Inject
    private EntityManagerFactory entityManagerFactory;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        // Инициализация MockMvc с контекстом приложения
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    private void createPost(String title) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Post post = new Post();
        post.setTitle(title);
        entityManager.persist(post);
        entityManager.getTransaction().commit();
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

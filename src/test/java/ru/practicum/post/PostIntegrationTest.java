package ru.practicum.post;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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

    private static final String TEST_POST_TITLE = "Test Post Title";

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
                                hasProperty("title", is(TEST_POST_TITLE))
                        )
                )))
                .andExpect(model().attribute("currentPage", 0))
                .andExpect(model().attribute("totalPages", 1))
                .andExpect(model().attribute("size", 10));
    }

    @Test
    public void createPost_ShouldCreatePost() throws Exception {
        mockMvc.perform(post("/post/")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "title1")
                        .param("content", "content1")
                        .param("image", "image1")
                        .param("tags", "tag1", "tag2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/post/"));

        List<Post> posts = postRepository.findAll();
        assertEquals(1, posts.size());
        assertEquals("title1", posts.getFirst().getTitle());
    }

    @Test
    public void showPost_ShouldShowPost() throws Exception {
        long postId = createPost();
        mockMvc.perform(get("/post/{id}", postId))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/show"))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attribute("post", hasProperty("title", is(TEST_POST_TITLE))));
    }

    @Test
    public void updatePost_ShouldUpdatePost() throws Exception {
        long postId = createPost();
        mockMvc.perform(patch("/post/{}", postId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", String.valueOf(postId))
                        .param("title", "Test Post Title updated")
                        .param("content", "Test Post Content updated")
                        .param("image", "Test Post Image updated")
                        .param("tags", "tag1", "tag2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/post/{id}"));

        Optional<Post> post = postRepository.findById(postId);
        assertTrue(post.isPresent());
        assertEquals("Test Post Title updated", post.get().getTitle());
        assertEquals("Test Post Content updated", post.get().getContent());
        assertEquals("Test Post Image updated", post.get().getImage());
        assertEquals(2, post.get().getTags().size());
    }

    @Test
    public void likePost_shouldLikedPost() throws Exception {
        long postId = createPost();
        mockMvc.perform(patch("/post/{id}/like", postId))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/show"))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attribute("post", hasProperty("likeCounter", is(1L))));
    }

    @Test
    public void deletePost_shouldDeletePost() throws Exception {
        long postId = createPost();
        mockMvc.perform(delete("/post/{id}", postId))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/post/"));
        Optional<Post> post = postRepository.findById(postId);
        assertTrue(post.isEmpty());
    }

    private long createPost() {
        Post post = new Post();
        post.setTitle(TEST_POST_TITLE);
        Post saved = postRepository.save(post);
        return saved.getId();
    }
}

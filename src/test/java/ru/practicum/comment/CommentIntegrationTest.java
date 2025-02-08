package ru.practicum.comment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.blog.PracticumBlogBootApplication;
import ru.practicum.blog.model.Comment;
import ru.practicum.blog.model.Post;
import ru.practicum.blog.repository.CommentRepository;
import ru.practicum.blog.repository.PostRepository;
import ru.practicum.config.EnablePostgresTest;

@SpringBootTest(classes = PracticumBlogBootApplication.class)
@AutoConfigureMockMvc
@EnablePostgresTest
@ActiveProfiles("test")
public class CommentIntegrationTest {

    private static final String TEST_POST_TITLE = "Test Post Title";
    private static final String TEST_COMMENT_CONTENT = "Test Comment Content";

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        postRepository.deleteAll();
    }

    @Test
    public void addComment_shouldAddComment() throws Exception {
        long postId = createPost().getId();
        mockMvc.perform(post("/comment")
                .param("content", TEST_COMMENT_CONTENT)
                .param("postId", String.valueOf(postId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + postId));

        Post post = postRepository.findById(postId).get();
        assertNotNull(post);
        assertEquals(1, post.getComments().size());
        assertEquals(TEST_COMMENT_CONTENT, post.getComments().getFirst().getContent());
    }

    @Test
    public void updateComment_shouldUpdateComment() throws Exception {
        Post post = createPost();
        long commentId = createComment(post);
        mockMvc.perform(patch("/comment/{id}", commentId)
                        .param("content", "Test Comment Content updated")
                        .param("postId", String.valueOf(post.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + post.getId()));

        Post postCreated = postRepository.findById(post.getId()).get();
        assertNotNull(postCreated);
        assertEquals(1, postCreated.getComments().size());
        assertEquals("Test Comment Content updated", postCreated.getComments().getFirst().getContent());
    }

    @Test
    public void deleteComment_shouldDeleteComment() throws Exception {
        Post post = createPost();
        long commentId = createComment(post);
        Post postCreated = postRepository.findById(post.getId()).get();
        assertNotNull(postCreated);
        assertEquals(1, postCreated.getComments().size());

        mockMvc.perform(delete("/comment/{id}", commentId)
                        .requestAttr("postId", String.valueOf(post.getId()))
                        .param("content", "Test Comment Content updated")
                        .param("postId", String.valueOf(post.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + post.getId()));

        Post postCreated1 = postRepository.findById(post.getId()).get();
        assertNotNull(postCreated1);
        assertEquals(0, postCreated1.getComments().size());
    }


    private Post createPost() {
        Post post = new Post();
        post.setTitle(TEST_POST_TITLE);
        return postRepository.save(post);
    }

    private long createComment(Post post) {
        Comment comment = new Comment();
        comment.setContent(TEST_COMMENT_CONTENT);
        comment.setPost(post);
        return commentRepository.save(comment).getId();
    }
}

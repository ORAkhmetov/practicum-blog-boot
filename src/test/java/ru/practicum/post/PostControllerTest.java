package ru.practicum.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.Model;
import ru.practicum.blog.controller.PostController;
import ru.practicum.blog.dto.CreatePostRequestDto;
import ru.practicum.blog.dto.PostDto;
import ru.practicum.blog.dto.PostShortDto;
import ru.practicum.blog.dto.UpdatePostRequestDto;
import ru.practicum.blog.service.PostService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostControllerTest {

    @Mock
    private PostService postService;

    @Mock
    private Model model;

    @InjectMocks
    private PostController postController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void allPost_ShouldCallServiceAndReturnView() {
        // Given
        int page = 0;
        int size = 10;
        String tag = null;
        Page<PostShortDto> posts = mock(Page.class);
        when(postService.getPosts(PageRequest.of(page, size))).thenReturn(posts);

        // When
        String result = postController.allPost(model, page, size, tag);

        // Then
        assertEquals("posts/index", result);
        verify(postService).getPosts(PageRequest.of(page, size));
        verify(model).addAttribute(eq("posts"), any());
        verify(model).addAttribute(eq("currentPage"), eq(page));
        verify(model).addAttribute(eq("totalPages"), any());
        verify(model).addAttribute(eq("size"), eq(size));
        verify(model).addAttribute(eq("tag"), eq(tag));
    }

    @Test
    void post_ShouldCallServiceAndRedirect() {
        // Given
        CreatePostRequestDto createPostRequestDto = new CreatePostRequestDto();
        createPostRequestDto.setTitle("Test Post");
        createPostRequestDto.setContent("Test Content");

        // When
        String result = postController.post(createPostRequestDto);

        // Then
        assertEquals("redirect:/post/", result);
        verify(postService).createPost(createPostRequestDto);
    }

    @Test
    void showPost_ShouldCallServiceAndReturnView() {
        // Given
        Long postId = 1L;
        PostDto postDto = new PostDto();
        postDto.setId(postId);
        when(postService.findById(postId)).thenReturn(postDto);

        // When
        String result = postController.showPost(postId, model);

        // Then
        assertEquals("posts/show", result);
        verify(postService).findById(postId);
        verify(model).addAttribute(eq("post"), eq(postDto));
    }

    @Test
    void updatePost_ShouldCallServiceAndRedirect() {
        // Given
        UpdatePostRequestDto updatePostRequestDto = new UpdatePostRequestDto();
        updatePostRequestDto.setId(1L);
        updatePostRequestDto.setTitle("Updated Title");
        updatePostRequestDto.setContent("Updated Content");

        // When
        String result = postController.updatePost(updatePostRequestDto);

        // Then
        assertEquals("redirect:/post/{id}", result);
        verify(postService).updatePost(updatePostRequestDto);
    }

    @Test
    void likePost_ShouldCallServiceAndReturnView() {
        // Given
        Long postId = 1L;
        PostDto postDto = new PostDto();
        postDto.setId(postId);
        when(postService.likePost(postId)).thenReturn(postDto);

        // When
        String result = postController.likePost(postId, model);

        // Then
        assertEquals("posts/show", result);
        verify(postService).likePost(postId);
        verify(model).addAttribute(eq("post"), eq(postDto));
    }

    @Test
    void deletePost_ShouldCallServiceAndRedirect() {
        // Given
        Long postId = 1L;

        // When
        String result = postController.deletePost(postId);

        // Then
        assertEquals("redirect:/post/", result);
        verify(postService).deletePost(postId);
    }
}

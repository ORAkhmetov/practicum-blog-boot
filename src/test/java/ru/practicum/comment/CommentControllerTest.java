package ru.practicum.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.blog.controller.CommentController;
import ru.practicum.blog.dto.CommentDto;
import ru.practicum.blog.service.CommentService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addComment_ShouldCallServiceAndRedirect() {
        // Given
        CommentDto commentDto = new CommentDto();
        commentDto.setPostId(1L);
        commentDto.setContent("Test comment");

        // When
        String result = commentController.addComment(commentDto);

        // Then
        assertEquals("redirect:/post/1", result);
        verify(commentService).addComment(commentDto);
    }

    @Test
    void updateComment_ShouldCallServiceAndRedirect() {
        // Given
        Long commentId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setPostId(1L);
        commentDto.setContent("Updated comment");

        // When
        String result = commentController.updateComment(commentId, commentDto);

        // Then
        assertEquals("redirect:/post/1", result);
        verify(commentService).editComment(commentId, "Updated comment");
    }

    @Test
    void deleteComment_ShouldCallServiceAndRedirect() {
        // Given
        Long commentId = 1L;
        Long postId = 1L;

        // When
        String result = commentController.deleteComment(commentId, postId);

        // Then
        assertEquals("redirect:/post/1", result);
        verify(commentService).deleteComment(commentId);
    }
}
package ru.practicum.comment;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.blog.converter.CommentConverter;
import ru.practicum.blog.dto.CommentDto;
import ru.practicum.blog.exception.EntityNotFoundException;
import ru.practicum.blog.model.Comment;
import ru.practicum.blog.model.Post;
import ru.practicum.blog.repository.CommentRepository;
import ru.practicum.blog.repository.PostRepository;
import ru.practicum.blog.service.CommentServiceImpl;

public class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentConverter commentConverter;

    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addComment_ShouldSaveComment_WhenPostExists() {
        // Given
        CommentDto commentDto = new CommentDto("Test comment", 100L);
        Comment comment = new Comment();
        Post post = new Post();

        when(commentConverter.convertToComment(commentDto)).thenReturn(comment);
        when(postRepository.findById(100L)).thenReturn(Optional.of(post));

        // When
        commentService.addComment(commentDto);

        // Then
        verify(commentRepository, times(1)).save(comment);
        assertThat(comment.getPost()).isEqualTo(post);
    }

    @Test
    void addComment_ShouldThrowException_WhenPostDoesNotExist() {
        // Given
        CommentDto commentDto = new CommentDto("Test comment", 100L);

        when(postRepository.findById(100L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> commentService.addComment(commentDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("No post found with id 100");

        verify(commentRepository, never()).save(any());
    }

    @Test
    void editComment_ShouldUpdateComment_WhenCommentExists() {
        // Given
        Comment existingComment = new Comment();
        existingComment.setContent("Old content");

        when(commentRepository.findById(1L)).thenReturn(Optional.of(existingComment));

        // When
        commentService.editComment(1L, "Updated content");

        // Then
        verify(commentRepository, times(1)).save(existingComment);
        assertThat(existingComment.getContent()).isEqualTo("Updated content");
    }

    @Test
    void editComment_ShouldDoNothing_WhenCommentDoesNotExist() {
        // Given
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        commentService.editComment(1L, "Updated content");

        // Then
        verify(commentRepository, never()).save(any());
    }

    @Test
    void deleteComment_ShouldCallRepositoryDelete_WhenCommentExists() {
        // When
        commentService.deleteComment(1L);

        // Then
        verify(commentRepository, times(1)).deleteById(1L);
    }
}

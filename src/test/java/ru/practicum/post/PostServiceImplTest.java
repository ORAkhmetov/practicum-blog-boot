package ru.practicum.post;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import ru.practicum.blog.converter.PostConverter;
import ru.practicum.blog.dto.*;
import ru.practicum.blog.exception.EntityNotFoundException;
import ru.practicum.blog.model.Post;
import ru.practicum.blog.model.Tag;
import ru.practicum.blog.repository.PostRepository;
import ru.practicum.blog.service.PostServiceImpl;
import ru.practicum.blog.service.TagService;

class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostConverter postConverter;

    @Mock
    private TagService tagService;

    @InjectMocks
    private PostServiceImpl postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPosts_ShouldReturnPageOfPostShortDto() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Post post = new Post();
        Page<Post> postPage = new PageImpl<>(List.of(post));

        when(postRepository.findAllByOrderByCreatedAtDesc(pageable)).thenReturn(postPage);
        when(postConverter.convertToPostShortDto(post)).thenReturn(new PostShortDto());

        // When
        Page<PostShortDto> result = postService.getPosts(pageable);

        // Then
        assertThat(result).isNotEmpty();
        verify(postRepository).findAllByOrderByCreatedAtDesc(pageable);
    }

    @Test
    void findById_ShouldReturnPostDto_WhenPostExists() {
        // Given
        Post post = new Post();
        post.setId(1L);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postConverter.convertToPostDto(post)).thenReturn(new PostDto());

        // When
        PostDto result = postService.findById(1L);

        // Then
        assertThat(result).isNotNull();
        verify(postRepository).findById(1L);
    }

    @Test
    void findById_ShouldThrowException_WhenPostNotFound() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> postService.findById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("No post found with id 1");
    }

    @Test
    void createPost_ShouldSavePost_WithTags() {
        // Given
        CreatePostRequestDto createPostRequestDto =
                new CreatePostRequestDto("Title", "Content", "image.jpg", List.of("java"));
        Post post = new Post();
        Set<Tag> tags = Set.of(new Tag("java"));

        when(postConverter.convertToPost(createPostRequestDto)).thenReturn(post);
        when(tagService.getOrCreate(createPostRequestDto.getTags())).thenReturn(tags);
        when(postRepository.save(post)).thenReturn(post);

        // When
        Post result = postService.createPost(createPostRequestDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTags()).isEqualTo(tags);
        verify(postRepository).save(post);
    }

    @Test
    void findPostByTag_ShouldReturnPageOfPostShortDto_WhenTagExists() {
        // Given
        String tagTitle = "java";
        Tag tag = new Tag(tagTitle);
        Pageable pageable = PageRequest.of(0, 10);
        Post post = new Post();
        Page<Post> postPage = new PageImpl<>(List.of(post));

        when(tagService.findTagByTitle(tagTitle)).thenReturn(Optional.of(tag));
        when(postRepository.findByTags(List.of(tag), pageable)).thenReturn(postPage);
        when(postConverter.convertToPostShortDto(post)).thenReturn(new PostShortDto());

        // When
        Page<PostShortDto> result = postService.findPostByTag(tagTitle, pageable);

        // Then
        assertThat(result).isNotEmpty();
        verify(postRepository).findByTags(List.of(tag), pageable);
    }

    @Test
    void findPostByTag_ShouldReturnEmptyPage_WhenTagDoesNotExist() {
        // Given
        String tagTitle = "java";
        Pageable pageable = PageRequest.of(0, 10);

        when(tagService.findTagByTitle(tagTitle)).thenReturn(Optional.empty());

        // When
        Page<PostShortDto> result = postService.findPostByTag(tagTitle, pageable);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void updatePost_ShouldUpdateAndSavePost_WhenPostExists() {
        // Given
        UpdatePostRequestDto updatePostRequestDto =
                new UpdatePostRequestDto(1L, "New Title", "New Content", "new-image.jpg", List.of("spring"));
        Post existingPost = new Post();
        Set<Tag> updatedTags = Set.of(new Tag("spring"));

        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));
        when(tagService.getOrCreate(updatePostRequestDto.getTags())).thenReturn(updatedTags);

        // When
        postService.updatePost(updatePostRequestDto);

        // Then
        assertThat(existingPost.getTitle()).isEqualTo("New Title");
        assertThat(existingPost.getTags()).isEqualTo(updatedTags);
        verify(postRepository).save(existingPost);
    }

    @Test
    void updatePost_ShouldThrowException_WhenPostNotFound() {
        // Given
        UpdatePostRequestDto updatePostRequestDto =
                new UpdatePostRequestDto(1L, "New Title", "New Content", "new-image.jpg", List.of("spring"));

        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> postService.updatePost(updatePostRequestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("No post found with id 1");
    }

    @Test
    void likePost_ShouldIncrementLikeCounter_WhenPostExists() {
        // Given
        Post post = new Post();
        post.setLikeCounter(5L);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);
        when(postConverter.convertToPostDto(post)).thenReturn(new PostDto());

        // When
        PostDto result = postService.likePost(1L);

        // Then
        assertThat(post.getLikeCounter()).isEqualTo(6);
        verify(postRepository).save(post);
    }

    @Test
    void deletePost_ShouldCallRepositoryDelete_WhenPostExists() {
        // When
        postService.deletePost(1L);

        // Then
        verify(postRepository).deleteById(1L);
    }
}
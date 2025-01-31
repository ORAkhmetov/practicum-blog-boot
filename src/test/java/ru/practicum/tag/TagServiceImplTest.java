package ru.practicum.tag;

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

import ru.practicum.blog.model.Tag;
import ru.practicum.blog.repository.TagRepository;
import ru.practicum.blog.service.TagServiceImpl;

class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findTagByTitle_ShouldReturnTag_WhenTagExists() {
        // Given
        String tagTitle = "java";
        Tag tag = new Tag(tagTitle);

        when(tagRepository.findTagByTitle(tagTitle)).thenReturn(Optional.of(tag));

        // When
        Optional<Tag> result = tagService.findTagByTitle(tagTitle);

        // Then
        assertThat(result).isPresent().contains(tag);
        verify(tagRepository).findTagByTitle(tagTitle);
    }

    @Test
    void findTagByTitle_ShouldReturnEmpty_WhenTagDoesNotExist() {
        // Given
        String tagTitle = "spring";

        when(tagRepository.findTagByTitle(tagTitle)).thenReturn(Optional.empty());

        // When
        Optional<Tag> result = tagService.findTagByTitle(tagTitle);

        // Then
        assertThat(result).isEmpty();
        verify(tagRepository).findTagByTitle(tagTitle);
    }

    @Test
    void findTagsByTitle_ShouldReturnSetOfTags() {
        // Given
        List<String> tagTitles = List.of("java", "spring");
        Set<Tag> tags = Set.of(new Tag("java"), new Tag("spring"));

        when(tagRepository.findTagsByTitleIn(anySet())).thenReturn(tags);

        // When
        Set<Tag> result = tagService.findTagsByTitle(tagTitles);

        // Then
        assertThat(result).hasSize(2).extracting(Tag::getTitle).containsExactlyInAnyOrder("java", "spring");
        verify(tagRepository).findTagsByTitleIn(anySet());
    }

    @Test
    void getOrCreate_ShouldReturnExistingTags_WhenAllTagsExist() {
        // Given
        List<String> tagTitles = List.of("java", "spring");
        Set<Tag> existingTags = Set.of(new Tag("java"), new Tag("spring"));

        when(tagRepository.findTagsByTitleIn(anySet())).thenReturn(existingTags);

        // When
        Set<Tag> result = tagService.getOrCreate(tagTitles);

        // Then
        assertThat(result).hasSize(2).extracting(Tag::getTitle).containsExactlyInAnyOrder("java", "spring");
        verify(tagRepository, never()).save(any());
    }
}
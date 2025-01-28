package ru.practicum.blog.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.blog.model.Tag;
import ru.practicum.blog.repository.TagRepository;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public Optional<Tag> findTagByTitle(String content) {
        return tagRepository.findTagByTitle(content);
    }

    @Override
    public Set<Tag> findTagsByTitle(List<String> content) {
        return tagRepository.findTagsByTitleIn(new HashSet<>(content));
    }

    @Override
    public Set<Tag> getOrCreate(List<String> content) {
        Set<String> tagsFiltered = content.stream()
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toSet());
        Set<Tag> tagsByTitleIn = tagRepository.findTagsByTitleIn(tagsFiltered);

        Set<String> tagSet = tagsByTitleIn.stream()
                .map(Tag::getTitle)
                .collect(Collectors.toSet());

        content.stream()
                .filter(tagFromPost -> !tagSet.contains(tagFromPost))
                .forEach(tagFromPost -> {
                    Tag saved = tagRepository.save(new Tag(tagFromPost));
                    tagsByTitleIn.add(saved);
                });

        return tagsByTitleIn;
    }
}

package ru.practicum.blog.service;

import java.util.List;
import java.util.Optional;

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
    public List<Tag> findTagsByTitle(List<String> content) {
        return tagRepository.findTagsByTitleIn(content);
    }
}

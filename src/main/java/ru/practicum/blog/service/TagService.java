package ru.practicum.blog.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import ru.practicum.blog.model.Tag;

public interface TagService {

    Optional<Tag> findTagByTitle(String content);

    Set<Tag> findTagsByTitle(List<String> content);

    Set<Tag> getOrCreate(List<String> content);
}

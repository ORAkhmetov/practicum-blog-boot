package ru.practicum.blog.service;

import java.util.List;
import java.util.Optional;

import ru.practicum.blog.model.Tag;

public interface TagService {

    Optional<Tag> findTagByTitle(String content);

    List<Tag> findTagsByTitle(List<String> content);
}

package ru.practicum.blog.service;

import ru.practicum.blog.dto.CommentDto;

public interface CommentService {

    void addComment(CommentDto comment);

    void editComment(Long id, String comment);

    void deleteComment(Long id);
}

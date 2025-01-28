package ru.practicum.blog.service;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.blog.converter.CommentConverter;
import ru.practicum.blog.dto.CommentDto;
import ru.practicum.blog.exception.EntityNotFoundException;
import ru.practicum.blog.model.Comment;
import ru.practicum.blog.model.Post;
import ru.practicum.blog.repository.CommentRepository;
import ru.practicum.blog.repository.PostRepository;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentConverter commentConverter;

    private final PostRepository postRepository;

    @Override
    public void addComment(CommentDto comment) {
        Comment converted = commentConverter.convertToComment(comment);
        Post post = postRepository.findById(comment.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("No post found with id " + comment.getPostId()));
        converted.setPost(post);
        commentRepository.save(converted);
    }

    @Override
    public void editComment(Long id, String comment) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (commentOptional.isPresent()) {
            Comment comment1 = commentOptional.get();
            comment1.setContent(comment);
            commentRepository.save(comment1);
        }
    }

    @Override
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}

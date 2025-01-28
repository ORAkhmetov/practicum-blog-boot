package ru.practicum.blog.converter;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.blog.dto.CommentDto;
import ru.practicum.blog.dto.CommentInPostDto;
import ru.practicum.blog.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentConverter {

    CommentInPostDto convertToCommentInPostDto(Comment comment);

    List<CommentInPostDto> convertToCommentInPostDtos(List<Comment> comments);

    @Mapping(source = "postId", target = "post", ignore = true)
    Comment convertToComment(CommentDto commentDto);
}

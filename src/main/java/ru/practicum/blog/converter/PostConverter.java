package ru.practicum.blog.converter;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.blog.dto.CreatePostRequestDto;
import ru.practicum.blog.dto.PostDto;
import ru.practicum.blog.model.Comment;
import ru.practicum.blog.model.Post;
import ru.practicum.blog.model.Tag;

@Mapper(componentModel = "spring")
public interface PostConverter {

    @Mapping(source = "tags", target = "tags", ignore = true)
    Post convertToPost(CreatePostRequestDto createPostRequestDto);

    //@Mapping(target = "tags", expression = "java(mapTagsToStrings(post.getTags()))")
    @Mapping(target = "commentsSize", expression = "java(post.getComments().size())")
    PostDto convertToPostDto(Post post);


    default List<String> mapTagsToStrings(Set<Tag> tags) {
        return tags != null ? tags.stream().map(Tag::getTitle).collect(Collectors.toList()) : Collections.emptyList();
    }

    // Маппинг для List<Comment> в List<String> (например, комментарии можно конвертировать в строки)
    default List<String> mapCommentsToStrings(List<Comment> comments) {
        return comments != null ? comments.stream().map(Comment::getContent).collect(Collectors.toList()) : Collections.emptyList();
    }
}

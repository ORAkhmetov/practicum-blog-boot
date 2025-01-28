package ru.practicum.blog.dto;

import java.util.List;

import lombok.Data;

@Data
public class PostDto {

    private long id;

    private String title;

    private String content;

    private String image;

    private Long likeCounter;

    private List<String> tags;

    private List<CommentInPostDto> comments;

    private String tagsString;
}

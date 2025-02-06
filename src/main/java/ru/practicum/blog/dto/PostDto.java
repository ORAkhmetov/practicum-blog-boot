package ru.practicum.blog.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

package ru.practicum.blog.dto;

import java.util.List;

import lombok.Data;

@Data
public class PostShortDto {

    private long id;

    private String title;

    private String content;

    private String image;

    private List<String> tags;

    private Long likeCounter;

    private Integer commentsSize;
}

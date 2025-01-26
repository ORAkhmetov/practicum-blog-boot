package ru.practicum.blog.dto;

import java.util.List;

import lombok.Data;

@Data
public class CreatePostRequestDto {

    private String title;

    private String content;

    private String image;

    private List<String> tags;
}

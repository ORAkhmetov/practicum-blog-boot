package ru.practicum.blog.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePostRequestDto {

    private Long id;

    private String title;

    private String content;

    private String image;

    private List<String> tags;
}

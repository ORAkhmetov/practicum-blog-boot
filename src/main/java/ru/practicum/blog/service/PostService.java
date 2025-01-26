package ru.practicum.blog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.blog.dto.CreatePostRequestDto;
import ru.practicum.blog.dto.PostDto;
import ru.practicum.blog.model.Post;

public interface PostService {

    Page<PostDto> getPosts(Pageable pageable);

    PostDto findById(long id);

    Post createPost(CreatePostRequestDto createPostRequestDto);

    Page<PostDto> findPostByTag(String tag, Pageable pageable);
}

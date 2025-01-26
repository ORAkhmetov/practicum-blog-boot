package ru.practicum.blog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.blog.dto.CreatePostRequestDto;
import ru.practicum.blog.dto.PostDto;
import ru.practicum.blog.dto.PostShortDto;
import ru.practicum.blog.dto.UpdatePostRequestDto;
import ru.practicum.blog.model.Post;

public interface PostService {

    Page<PostShortDto> getPosts(Pageable pageable);

    PostDto findById(long id);

    Post createPost(CreatePostRequestDto createPostRequestDto);

    Page<PostShortDto> findPostByTag(String tag, Pageable pageable);

    void updatePost(UpdatePostRequestDto postDto);

    PostDto likePost(long postId);

    void deletePost(long postId);
}

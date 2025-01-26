package ru.practicum.blog.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.blog.converter.PostConverter;
import ru.practicum.blog.dto.CreatePostRequestDto;
import ru.practicum.blog.dto.PostDto;
import ru.practicum.blog.dto.PostShortDto;
import ru.practicum.blog.exception.EntityNotFoundException;
import ru.practicum.blog.model.Post;
import ru.practicum.blog.model.Tag;
import ru.practicum.blog.repository.PostRepository;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    private final PostConverter postConverter;

    private final TagService tagService;

    @Override
    public Page<PostShortDto> getPosts(Pageable pageable) {
        Page<Post> postListPage = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        return postListPage.map(postConverter::convertToPostShortDto);
    }

    @Override
    public PostDto findById(long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No post found with id " + id));
        return postConverter.convertToPostDto(post);
    }

    @Override
    @Transactional()
    public Post createPost(CreatePostRequestDto createPostRequestDto) {
        Post post = postConverter.convertToPost(createPostRequestDto);
        Set<Tag> tagsByContent = tagService.getOrCreate(createPostRequestDto.getTags());
        post.setTags(tagsByContent);
        return postRepository.save(post);
    }

    @Override
    public Page<PostShortDto> findPostByTag(String tag, Pageable pageable) {
        Optional<Tag> tagByTitle = tagService.findTagByTitle(tag);
        if (tagByTitle.isPresent()) {
            Page<Post> tagsContaining = postRepository.findByTags(List.of(tagByTitle.get()), pageable);
            return tagsContaining.map(postConverter::convertToPostShortDto);
        } else {
            return Page.empty(pageable);
        }
    }

    @Override
    public PostDto likePost(long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("No post found with id " + postId));
        post.setLikeCounter(post.getLikeCounter() + 1);
        Post saved = postRepository.save(post);
        return postConverter.convertToPostDto(saved);
    }
}

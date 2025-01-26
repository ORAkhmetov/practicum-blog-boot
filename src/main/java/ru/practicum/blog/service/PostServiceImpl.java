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
    public Page<PostDto> getPosts(Pageable pageable) {
        Page<Post> postListPage = postRepository.findAll(pageable);
        return postListPage.map(postConverter::convertToPostDto);
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
    public Page<PostDto> findPostByTag(String tag, Pageable pageable) {
        Optional<Tag> tagByTitle = tagService.findTagByTitle(tag);
        if (tagByTitle.isPresent()) {
            Page<Post> tagsContaining = postRepository.findByTags(List.of(tagByTitle.get()), pageable);
            return tagsContaining.map(postConverter::convertToPostDto);
        } else {
            return Page.empty(pageable);
        }
    }
}

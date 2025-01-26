package ru.practicum.blog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.blog.dto.CreatePostRequestDto;
import ru.practicum.blog.dto.PostDto;
import ru.practicum.blog.dto.PostShortDto;
import ru.practicum.blog.service.PostService;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class BlogController {

    private final PostService postService;


    @GetMapping()
    public String allPost(Model model,
                          @RequestParam(name = "page", defaultValue = "0") int page,
                          @RequestParam(name = "size", defaultValue = "10") int size,
                          @RequestParam(name = "tag", required = false) String tag) {
        Page<PostShortDto> posts;
        if (tag == null) {
            posts = postService.getPosts(PageRequest.of(page, size));
        } else {
            posts = postService.findPostByTag(tag, PageRequest.of(page, size));
        }
        model.addAttribute("posts", posts.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", posts.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("tag", tag);
        return "posts/index";
    }

    @PostMapping()
    public String post(@ModelAttribute("post") CreatePostRequestDto createPostRequestDto) {
        postService.createPost(createPostRequestDto);
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String showPost(@PathVariable(name = "id") Long id, Model model) {
        PostDto postDto = postService.findById(id);
        model.addAttribute("post", postDto);
        return "posts/show";
    }

    @PatchMapping("/{id}/like")
    public String likePost(@PathVariable(name = "id") Long id, Model model) {
        PostDto postDto = postService.likePost(id);
        model.addAttribute("post", postDto);
        return "posts/show";
    }
}

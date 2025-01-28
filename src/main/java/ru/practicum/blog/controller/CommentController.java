package ru.practicum.blog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.blog.dto.CommentDto;
import ru.practicum.blog.service.CommentService;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public String addComment(@ModelAttribute CommentDto commentDto) {
        commentService.addComment(commentDto);
        return "redirect:/post/" + commentDto.getPostId();
    }

    @PatchMapping("/{id}")
    public String updateComment(@PathVariable(name = "id") Long id,
                                @ModelAttribute("comment") CommentDto commentDto) {
        commentService.editComment(id, commentDto.getContent());
        return "redirect:/post/" + commentDto.getPostId();
    }

    @DeleteMapping("/{id}")
    public String deleteComment(@PathVariable(name = "id") Long id,
                                @RequestParam(name = "postId") Long postId) {
        commentService.deleteComment(id);
        return "redirect:/post/" + postId;
    }

}

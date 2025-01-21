package ru.practicum.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.blog.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}

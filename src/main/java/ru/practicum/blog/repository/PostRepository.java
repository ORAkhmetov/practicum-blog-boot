package ru.practicum.blog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.blog.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAll();
}

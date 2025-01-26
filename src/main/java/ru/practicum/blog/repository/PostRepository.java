package ru.practicum.blog.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.blog.model.Post;
import ru.practicum.blog.model.Tag;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByTags(List<Tag> tags, Pageable pageable);
}

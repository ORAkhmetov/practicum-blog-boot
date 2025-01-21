package ru.practicum.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.blog.model.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
}

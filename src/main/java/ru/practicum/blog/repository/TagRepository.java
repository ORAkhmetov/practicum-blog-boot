package ru.practicum.blog.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.practicum.blog.mapper.TagRowMapper;
import ru.practicum.blog.model.Tag;

import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class TagRepository {

    private final JdbcTemplate jdbcTemplate;

    private final TagRowMapper tagRowMapper;

    public Tag save(Tag tag) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO tag (title) VALUES (?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id"});
            ps.setString(1, tag.getTitle());
            return ps;
        }, keyHolder);

        tag.setId(keyHolder.getKey().longValue());
        return tag;
    }


    public Optional<Tag> findTagByTitle(String content) {
        String sql = "SELECT * FROM tag WHERE title = ?";
        return jdbcTemplate.query(sql, tagRowMapper, content).stream().findFirst();
    }

    public Set<Tag> findTagsByTitleIn(Set<String> titles) {
        if (titles.isEmpty()) {
            return Collections.emptySet();
        }

        String placeholders = String.join(",", Collections.nCopies(titles.size(), "?"));

        String sql = "SELECT * FROM tag WHERE title IN (" + placeholders + ")";

        return new HashSet<>(jdbcTemplate.query(sql, tagRowMapper, titles.toArray()));
    }
}

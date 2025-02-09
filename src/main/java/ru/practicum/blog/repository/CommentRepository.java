package ru.practicum.blog.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.practicum.blog.mapper.CommentRowMapper;
import ru.practicum.blog.mapper.PostRowMapper;
import ru.practicum.blog.model.Comment;
import ru.practicum.blog.model.Post;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    private final CommentRowMapper commentRowMapper;

    private final PostRowMapper postRowMapper;

    public Comment save(Comment comment) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        if (comment.getPost() == null || comment.getPost().getId() == 0) {
            throw new IllegalArgumentException("Comment must be associated with a post.");
        }

        if (comment.getId() == 0) {
            String sql = "INSERT INTO comment (content, post_id, created_at) VALUES (?, ?, ?)";
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id"});
                ps.setString(1, comment.getContent());
                ps.setLong(2, comment.getPost().getId());
                ps.setTimestamp(3, Timestamp.valueOf(comment.getCreatedAt().toLocalDateTime()));
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() != null) {
                comment.setId(keyHolder.getKey().longValue());
            }
        } else {
            String sql = "UPDATE comment SET content = ?, post_id = ?, created_at = ? WHERE id = ?";
            jdbcTemplate.update(sql, comment.getContent(), comment.getPost().getId(), comment.getCreatedAt(), comment.getId());
        }

        String postSql = "SELECT * FROM post WHERE id = ?";
        Post post = jdbcTemplate.queryForObject(postSql, postRowMapper, comment.getPost().getId());

        comment.setPost(post);

        return comment;
    }



    public Optional<Comment> findById(Long id) {
        String sql = "SELECT * FROM comment WHERE id = ?";

        List<Comment> comments = jdbcTemplate.query(sql, commentRowMapper, id);

        if (!comments.isEmpty()) {
            Comment comment = comments.get(0);

            String postSql = "SELECT * FROM post WHERE id = ?";
            Post post = jdbcTemplate.queryForObject(postSql, postRowMapper, comment.getPost().getId());

            comment.setPost(post);

            return Optional.of(comment);
        }

        return Optional.empty();
    }


    public void deleteById(Long id) {
        String sql = "DELETE FROM comment WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}

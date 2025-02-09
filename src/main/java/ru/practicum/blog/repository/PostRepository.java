package ru.practicum.blog.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.blog.mapper.CommentRowMapper;
import ru.practicum.blog.mapper.PostRowMapper;
import ru.practicum.blog.model.Comment;
import ru.practicum.blog.model.Post;
import ru.practicum.blog.model.Tag;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    private final JdbcTemplate jdbcTemplate;

    private final PostRowMapper postRowMapper;

    private final CommentRowMapper commentRowMapper;

    public Post save(Post post) {
        String sql;
        String deleteTagsSql;
        String insertTagSql;
        String commentSql;

        if (post.getId() > 0) {
            sql = "UPDATE post SET title = ?, content = ?, image = ?, like_counter = ?, created_at = ? WHERE id = ?";
            jdbcTemplate.update(sql, post.getTitle(), post.getContent(), post.getImage(),
                    Math.toIntExact(post.getLikeCounter()), Timestamp.valueOf(post.getCreatedAt().toLocalDateTime()), post.getId());

            deleteTagsSql = "DELETE FROM post_tag WHERE post_id = ?";
            jdbcTemplate.update(deleteTagsSql, post.getId());

            Set<Tag> tags = post.getTags() != null ? post.getTags() : new HashSet<>();
            insertTagSql = "INSERT INTO post_tag (post_id, tag_id) VALUES (?, ?)";
            for (Tag tag : tags) {
                jdbcTemplate.update(insertTagSql, post.getId(), tag.getId());
            }

            commentSql = "SELECT * FROM comment WHERE post_id = ?";
            List<Comment> comments = jdbcTemplate.query(commentSql, commentRowMapper, post.getId());
            post.setComments(comments);

            return post;
        } else {
            sql = "INSERT INTO post (title, content, image, like_counter, created_at) VALUES (?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id"});
                ps.setString(1, post.getTitle());
                ps.setString(2, post.getContent());
                ps.setString(3, post.getImage());
                ps.setInt(4, Math.toIntExact(post.getLikeCounter()));
                ps.setTimestamp(5, Timestamp.valueOf(post.getCreatedAt().toLocalDateTime()));
                return ps;
            }, keyHolder);

            Long generatedId = keyHolder.getKey().longValue();
            post.setId(generatedId);

            Set<Tag> tags = post.getTags() != null ? post.getTags() : new HashSet<>();
            insertTagSql = "INSERT INTO post_tag (post_id, tag_id) VALUES (?, ?)";
            for (Tag tag : tags) {
                jdbcTemplate.update(insertTagSql, generatedId, tag.getId());
            }
            post.setTags(tags);

            commentSql = "SELECT * FROM comment WHERE post_id = ?";
            List<Comment> comments = jdbcTemplate.query(commentSql, commentRowMapper, generatedId);
            post.setComments(comments);

            return post;
        }
    }

    public Optional<Post> findById(Long id) {
        String sql = "SELECT p.*, c.id as comment_id, c.content as comment_content " +
                "FROM post p " +
                "LEFT JOIN comment c ON p.id = c.post_id " +
                "WHERE p.id = ?";

        List<Post> posts = jdbcTemplate.query(sql, postRowMapper, id);

        if (posts.isEmpty()) {
            return Optional.empty();
        }

        Post post = posts.get(0);

        String commentSql = "SELECT * FROM comment WHERE post_id = ?";
        List<Comment> comments = jdbcTemplate.query(commentSql, commentRowMapper, post.getId());
        post.setComments(comments);

        String tagSql = "SELECT t.* FROM tag t " +
                "JOIN post_tag pt ON t.id = pt.tag_id " +
                "WHERE pt.post_id = ?";
        List<Tag> tags = jdbcTemplate.query(tagSql, (rs, rowNum) -> {
            Tag tag = new Tag();
            tag.setId(rs.getLong("id"));
            tag.setTitle(rs.getString("title"));
            return tag;
        }, post.getId());
        post.setTags(new HashSet<>(tags));

        return Optional.of(post);
    }

    public void deleteById(long postId) {
        String deleteTagsSql = "DELETE FROM post_tag WHERE post_id = ?";
        jdbcTemplate.update(deleteTagsSql, postId);

        String deletePostSql = "DELETE FROM post WHERE id = ?";
        jdbcTemplate.update(deletePostSql, postId);
    }

    public Page<Post> findByTags(List<Long> tagIds, Pageable pageable) {
        String inSql = String.join(",", tagIds.stream().map(String::valueOf).toArray(String[]::new));

        String sql = "SELECT p.*, c.id AS comment_id, c.content AS comment_content " +
                "FROM post p " +
                "LEFT JOIN comment c ON p.id = c.post_id " +
                "JOIN post_tag pt ON p.id = pt.post_id " +
                "WHERE pt.tag_id IN (" + inSql + ") " +
                "GROUP BY p.id, c.id " +
                "ORDER BY p.created_at DESC LIMIT ? OFFSET ?";

        List<Post> posts = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Post post = postRowMapper.mapRow(rs, rowNum);

            Long commentId = rs.getLong("comment_id");
            if (commentId != 0) {
                Comment comment = new Comment();
                comment.setId(commentId);
                comment.setContent(rs.getString("comment_content"));
                post.setComments(Collections.singletonList(comment));
            }

            return post;
        }, pageable.getPageSize(), pageable.getOffset());

        posts.forEach(post -> {
            String commentSql = "SELECT * FROM comment WHERE post_id = ?";
            List<Comment> comments = jdbcTemplate.query(commentSql, commentRowMapper, post.getId());
            post.setComments(comments);
        });

        return new PageImpl<>(posts, pageable, posts.size());
    }

    public Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable) {
        String sql = "SELECT p.*, c.id AS comment_id, c.content AS comment_content " +
                "FROM post p " +
                "LEFT JOIN comment c ON p.id = c.post_id " +
                "ORDER BY p.created_at DESC LIMIT ? OFFSET ?";

        List<Post> posts = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Post post = postRowMapper.mapRow(rs, rowNum);

            Long commentId = rs.getLong("comment_id");
            if (commentId != 0) {
                Comment comment = new Comment();
                comment.setId(commentId);
                comment.setContent(rs.getString("comment_content"));
                post.setComments(Collections.singletonList(comment));
            }

            return post;
        }, pageable.getPageSize(), pageable.getOffset());

        posts.forEach(post -> {
            String commentSql = "SELECT * FROM comment WHERE post_id = ?";
            List<Comment> comments = jdbcTemplate.query(commentSql, commentRowMapper, post.getId());
            post.setComments(comments);
        });

        return new PageImpl<>(posts, pageable, posts.size());
    }

    public void deleteAll() {
        String deleteTagsSql = "DELETE FROM post_tag";
        jdbcTemplate.update(deleteTagsSql);

        String deletePostsSql = "DELETE FROM post";
        jdbcTemplate.update(deletePostsSql);
    }

    public List<Post> findAll() {
        String sql = "SELECT * FROM post";
        return jdbcTemplate.query(sql, postRowMapper);
    }
}

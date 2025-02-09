package ru.practicum.blog.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.practicum.blog.model.Post;

@Component
public class PostRowMapper implements RowMapper<Post> {

    @Override
    public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setTitle(rs.getString("title"));
        post.setContent(rs.getString("content"));
        post.setImage(rs.getString("image"));
        post.setLikeCounter(rs.getLong("like_counter"));
        post.setCreatedAt(rs.getObject("created_at", java.time.OffsetDateTime.class));
        return post;
    }
}
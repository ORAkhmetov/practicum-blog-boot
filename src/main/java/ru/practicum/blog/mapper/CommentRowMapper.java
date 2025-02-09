package ru.practicum.blog.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.practicum.blog.model.Comment;
import ru.practicum.blog.model.Post;

@Component
public class CommentRowMapper implements RowMapper<Comment> {

    @Override
    public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
        Comment comment = new Comment();

        comment.setId(rs.getLong("id"));
        comment.setContent(rs.getString("content"));
        comment.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));

        Long postId = rs.getLong("post_id");
        if (!rs.wasNull()) {
            Post post = new Post();
            post.setId(postId);
            comment.setPost(post);
        }
        return comment;
    }

}

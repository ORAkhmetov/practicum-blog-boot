ALTER TABLE comment
    ADD COLUMN created_at timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL;

CREATE INDEX idx_post_created_at ON post(created_at);

CREATE INDEX idx_comment_created_at ON comment(created_at);
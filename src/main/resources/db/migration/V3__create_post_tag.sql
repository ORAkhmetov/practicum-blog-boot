CREATE TABLE post_tag (
    post_id bigint REFERENCES post(id),
    tag_id bigint REFERENCES tag(id)
);
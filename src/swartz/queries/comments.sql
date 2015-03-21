-- name: get-post-comments
-- Get comments for a post given the post id.
select * from comments where post_id = ?

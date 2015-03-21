-- name: get-post-comments
-- Get comments for a post given the post id.
select * from comments where post_id = :post-id

-- name: create-comment!
-- Create a comment with the given content, user_id, post_id, and parent_id.
insert into comments (content, user_id, post_id, parent_id)
       values (:content, :user_id, :post_id, :parent_id)

-- name: get-comment-by-id
-- Get a comment by the given id.
select * from comments where id = :id

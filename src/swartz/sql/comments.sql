-- name: find-by-post
-- Get comments for a post given the post id.
select * from comments where post_id = :post-id

-- name: count-all
-- Get the total number of comments.
select count(*) from comments

-- name: create<!
-- Create a comment with the given content, user_id, post_id, and parent_id.
insert into comments (content, user_id, post_id, parent_id)
       values (:content, :user_id, :post_id, :parent_id)

-- name: find-by-id
-- Get a comment by the given id.
select * from comments where id = :id limit 1

-- name: delete-all!
-- Delete all comments.
delete from comments

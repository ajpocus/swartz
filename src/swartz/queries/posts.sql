-- name: all-posts
-- Get all posts, including the creator's username.
select p.*, u.username from posts p
       join users u on p.user_id = u.id

-- name: get-post-by-id
-- Get a post by id, including the creator's username.
select p.*, u.username from posts p
       join users u on p.user_id = u.id
       where p.id = ?

-- name: create-post
--

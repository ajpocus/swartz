-- name: all-posts
-- Get all posts, including the creator's username.
select p.*, u.username, count(c.*) comment_count from posts p
       join users u on p.user_id = u.id
       join comments c on c.post_id = p.id

-- name: get-post-by-id
-- Get a post by id, including the creator's username.
select p.*, u.username, count(c.*) comment_count from posts p
       join users u on p.user_id = u.id
       join comments c on c.post_id = p.id
       where p.id = :post_id

-- name: create-post!
-- Create a post with the given title, URL, content, and user id.
insert into posts (title, url, content user_id)
       values (:title, :url, :content, :user_id)

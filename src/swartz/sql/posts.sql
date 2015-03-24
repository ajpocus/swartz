-- name: find-all
-- Get all posts, including the creator's username.
select p.*,
       (select username from users where id = p.user_id),
       (select count(*) from comments where post_id = p.id) as comment_count
       from posts p

-- name: count-all
-- Get the total number of posts
select count(*) from posts

-- name: find-by-id
-- Get a post by id, including the creator's username.
select p.*,
       (select username from users where id = p.user_id),
       (select count(*) from comments where post_id = p.id) as comment_count
       from posts p
       where p.id = :post_id
       limit 1

-- name: create<!
-- Create a post with the given title, URL, content, and user id.
insert into posts (title, url, content, user_id)
       values (:title, :url, :content, :user_id)

-- name: delete-all!
-- Delete all posts.
delete from posts

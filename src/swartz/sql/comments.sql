-- name: find-by-post
-- Get comments for a post given the post id.
select c.*, cl.depth, u.username from comments c
       join comments_closure cl
       on cl.child_id = c.id
       join users u
       on u.id = c.user_id
       where exists
             (select * from comments c2
                     where cl.parent_id = c2.id
                           and c2.parent_id is null
                           and c2.post_id = :post_id)
       order by rank asc

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

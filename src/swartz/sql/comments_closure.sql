-- name: find-all
-- Get all comment closure entries.
select * from comments_closure

-- name: count-all
-- Get the number of comment closure entries.
select count(*) from comments_closure

-- name: find-by-parent-id
-- Get comment closure entry by parent id.
select * from comments_closure where parent_id = :parent_id

-- name: find-by-child-id
-- Get comment closure entry by child id.
select * from comments_closure where child_id = :child_id

-- name: find-by-triplet
select * from comments_closure
       where parent_id = :parent_id
       and child_id = :child_id
       and depth = :depth

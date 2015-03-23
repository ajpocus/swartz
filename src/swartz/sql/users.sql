-- name: find-all
-- Get all of the users in the database.
select * from users

-- name: create<!
-- Create a user with a username and password.
insert into users (username, password)
       values (:username, :password)

-- name: find-by-username
-- Find a user by the given username.
select * from users where username = :username

-- name: delete-all!
-- Delete all users.
delete from users

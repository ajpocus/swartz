-- name: all-users
-- Get all of the users in the database.
select * from users

-- name: create-user!
-- Create a user with a username and password.
insert into users (username, password)
       values (:username, :password)

-- name: get-user-by-username
-- Find a user by the given username.
select * from users where username = :username
